package ro.parcare.parcare_automatizate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.parcare.parcare_automatizate.enums.CanalPlata;
import ro.parcare.parcare_automatizate.enums.MetodaPlata;
import ro.parcare.parcare_automatizate.enums.StareSesiune;
import ro.parcare.parcare_automatizate.enums.TipLoc;
import ro.parcare.parcare_automatizate.enums.TipPerioada;
import ro.parcare.parcare_automatizate.enums.TipZona;
import ro.parcare.parcare_automatizate.factory.AbonamentFactoryImpl;
import ro.parcare.parcare_automatizate.factory.ParcareFactoryImpl;
import ro.parcare.parcare_automatizate.factory.SesiuneFactoryImpl;
import ro.parcare.parcare_automatizate.model.Abonament;
import ro.parcare.parcare_automatizate.model.NumarInmatriculare;
import ro.parcare.parcare_automatizate.model.Parcare;
import ro.parcare.parcare_automatizate.model.Plata;
import ro.parcare.parcare_automatizate.model.SesiuneParcare;
import ro.parcare.parcare_automatizate.model.Vehicul;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Testeaza fluxul principal: intrare, plata, iesire.
class ParcareWorkflowServiceTest {

    // 9 noiembrie 2026 este luni (tarif: 5 lei pe ora, 25 lei pe zi)
    private final LocalDate luni = LocalDate.of(2026, 11, 9);
    private final NumarInmatriculare numar = new NumarInmatriculare("IS 12 ABC");

    private Parcare parcare;
    private NotificareService notificari;
    private AbonamentWorkflowService abonamente;
    private ParcareWorkflowService workflow;

    // se ruleaza inainte de fiecare test, ca fiecare test sa inceapa cu o parcare noua
    @BeforeEach
    void pregateste() {
        // 2 locuri abonati, 2 locuri ocazionali, 1 loc dizabilitati
        parcare = new ParcareFactoryImpl().creeazaParcare("Parcare Centru", "Str. Principala 1", 2, 2, 1, 70);

        CalculService calcul = new CalculServiceImpl();
        ValidareService validare = new ValidareServiceImpl();
        AuditService audit = new AuditServiceImpl();
        notificari = new NotificareServiceImpl();

        abonamente = new AbonamentWorkflowServiceImpl(parcare, new AbonamentFactoryImpl(),
                validare, calcul, notificari, audit);
        workflow = new ParcareWorkflowServiceImpl(parcare, new SesiuneFactoryImpl(),
                calcul, validare, abonamente, notificari, audit);
    }

    @Test
    void intrareaOcupaUnLoc() {
        SesiuneParcare sesiune = workflow.intrare(numar, luni.atTime(10, 0));

        assertEquals(StareSesiune.ACTIVA, sesiune.getStare());
        // zona ocazionalilor are 3 locuri (2 standard + 1 dizabilitati), acum mai sunt 2 libere
        assertEquals(2, workflow.locuriLibere(TipZona.OCAZIONALI));
        assertNotNull(sesiune.getDataCreare());
    }

    @Test
    void aceeasiMasinaNuPoateIntraDeDouaOri() {
        workflow.intrare(numar, luni.atTime(10, 0));
        assertThrows(IllegalStateException.class, () -> workflow.intrare(numar, luni.atTime(10, 5)));
    }

    @Test
    void parcareaPlinaRefuzaIntrarea() {
        workflow.intrare(new NumarInmatriculare("IS 11 AAA"), luni.atTime(9, 0));
        workflow.intrare(new NumarInmatriculare("IS 22 BBB"), luni.atTime(9, 5));
        // a ramas doar locul pentru dizabilitati, iar masina nu are card
        assertThrows(IllegalStateException.class, () -> workflow.intrare(numar, luni.atTime(10, 0)));
    }

    @Test
    void soferulCuCardPrimesteLoculPentruDizabilitati() {
        workflow.inregistreazaVehicul(new Vehicul(numar, LocalDate.of(2027, 1, 1)));
        SesiuneParcare sesiune = workflow.intrare(numar, luni.atTime(10, 0));

        assertEquals(TipLoc.DIZABILITATI, parcare.gasesteLoc(sesiune.getIdLoc()).getTip());
    }

    @Test
    void faraPlataBarieraNuSeDeschide() {
        workflow.intrare(numar, luni.atTime(10, 0));
        assertFalse(workflow.iesire(numar, luni.atTime(10, 30)));
    }

    @Test
    void plataPeOreSiIesireaInCeleCincisprezeceMinute() {
        workflow.intrare(numar, luni.atTime(10, 0));

        // 1 h 10 min = 2 ore = 10 lei, valabil pana la 12:00
        Plata plata = workflow.plateste(numar, TipPerioada.ORA, MetodaPlata.CARD, CanalPlata.TONOMAT,
                true, luni.atTime(11, 10));
        assertEquals(new BigDecimal("10.00"), plata.getSuma());

        // iese la 12:10, in cele 15 minute de gratie
        assertTrue(workflow.iesire(numar, luni.atTime(12, 10)));
        // locul s-a eliberat: toate cele 3 locuri sunt din nou libere
        assertEquals(3, workflow.locuriLibere(TipZona.OCAZIONALI));
    }

    @Test
    void dupaGratieSePlatesteTaxaDeIntarziere() {
        workflow.intrare(numar, luni.atTime(10, 0));
        workflow.plateste(numar, TipPerioada.ORA, MetodaPlata.CARD, CanalPlata.TONOMAT, true, luni.atTime(11, 10));

        // la 12:30 a depasit 12:00 + 15 minute
        assertFalse(workflow.iesire(numar, luni.atTime(12, 30)));

        // plateste ora 12:00 - 13:00 (5 lei) + taxa de intarziere (25 lei)
        assertEquals(new BigDecimal("30.00"), workflow.sumaDePlata(numar, TipPerioada.ORA, luni.atTime(12, 30)));
        workflow.plateste(numar, TipPerioada.ORA, MetodaPlata.CARD, CanalPlata.TONOMAT, true, luni.atTime(12, 30));

        assertTrue(workflow.iesire(numar, luni.atTime(12, 35)));
    }

    @Test
    void plataEsuataTrimiteNotificareSiNuDeschideBariera() {
        workflow.intrare(numar, luni.atTime(10, 0));
        workflow.plateste(numar, TipPerioada.ORA, MetodaPlata.CARD, CanalPlata.TONOMAT, false, luni.atTime(11, 0));

        assertFalse(workflow.iesire(numar, luni.atTime(11, 5)));
        assertEquals(1, notificari.nerezolvate().size());
    }

    @Test
    void abonatulIntraPeLoculLuiSiIeseFaraPlata() {
        Abonament abonament = abonamente.cumparaAbonament(numar, true, LocalDate.of(2026, 11, 1));
        SesiuneParcare sesiune = workflow.intrare(numar, luni.atTime(8, 0));

        assertEquals(abonament.getIdLoc(), sesiune.getIdLoc());
        assertTrue(workflow.iesire(numar, luni.atTime(18, 0)));
    }
}

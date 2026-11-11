package ro.parcare.parcare_automatizate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.parcare.parcare_automatizate.enums.CanalPlata;
import ro.parcare.parcare_automatizate.enums.MetodaPlata;
import ro.parcare.parcare_automatizate.enums.StareLoc;
import ro.parcare.parcare_automatizate.enums.TipNotificare;
import ro.parcare.parcare_automatizate.enums.TipPerioada;
import ro.parcare.parcare_automatizate.enums.TipZi;
import ro.parcare.parcare_automatizate.factory.AbonamentFactoryImpl;
import ro.parcare.parcare_automatizate.factory.ParcareFactoryImpl;
import ro.parcare.parcare_automatizate.factory.SesiuneFactoryImpl;
import ro.parcare.parcare_automatizate.model.Abonament;
import ro.parcare.parcare_automatizate.model.NumarInmatriculare;
import ro.parcare.parcare_automatizate.model.Parcare;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AdministrareWorkflowServiceTest {

    private final LocalDate luni = LocalDate.of(2026, 11, 9);
    private final NumarInmatriculare numar = new NumarInmatriculare("IS 12 ABC");

    private Parcare parcare;
    private AbonamentWorkflowService abonamente;
    private ParcareWorkflowService workflow;
    private AdministrareWorkflowService administrare;

    @BeforeEach
    void pregateste() {
        parcare = new ParcareFactoryImpl().creeazaParcare("Parcare Centru", "Str. Principala 1", 2, 2, 1, 70);

        CalculService calcul = new CalculServiceImpl();
        ValidareService validare = new ValidareServiceImpl();
        AuditService audit = new AuditServiceImpl();
        NotificareService notificari = new NotificareServiceImpl();

        abonamente = new AbonamentWorkflowServiceImpl(parcare, new AbonamentFactoryImpl(),
                validare, calcul, notificari, audit);
        workflow = new ParcareWorkflowServiceImpl(parcare, new SesiuneFactoryImpl(),
                calcul, validare, abonamente, notificari, audit);
        administrare = new AdministrareWorkflowServiceImpl(parcare, validare, calcul,
                workflow, abonamente, notificari, audit);
    }

    @Test
    void locRezervatOcupatDeAltcinevaGenereazaNotificare() {
        // regula A7: abonatul nu e in parcare, dar senzorul spune ca locul lui e ocupat
        Abonament abonament = abonamente.cumparaAbonament(numar, true, LocalDate.of(2026, 11, 1));
        int numarLoc = parcare.gasesteLoc(abonament.getIdLoc()).getNumar();

        administrare.raporteazaSenzor(numarLoc, StareLoc.OCUPAT, luni.atTime(10, 0));

        assertEquals(1, administrare.notificariNerezolvate().size());
        assertEquals(TipNotificare.LOC_OCUPAT_INCORECT, administrare.notificariNerezolvate().get(0).getTip());
    }

    @Test
    void abonatulParcatPeAltLocGenereazaNotificare() {
        // regula A8: abonatul a intrat, dar senzorul spune ca locul lui e liber
        Abonament abonament = abonamente.cumparaAbonament(numar, true, LocalDate.of(2026, 11, 1));
        workflow.intrare(numar, luni.atTime(8, 0));
        int numarLoc = parcare.gasesteLoc(abonament.getIdLoc()).getNumar();

        administrare.raporteazaSenzor(numarLoc, StareLoc.LIBER, luni.atTime(8, 5));

        assertEquals(1, administrare.notificariNerezolvate().size());
    }

    @Test
    void murdariaPestePragAnuntaAdministratorul() {
        administrare.raporteazaMurdarie(50, luni.atTime(9, 0));
        assertEquals(0, administrare.notificariNerezolvate().size());

        administrare.raporteazaMurdarie(85, luni.atTime(9, 30));
        assertEquals(1, administrare.notificariNerezolvate().size());
    }

    @Test
    void notificareaRezolvataNuMaiApare() {
        administrare.raporteazaMurdarie(85, luni.atTime(9, 30));
        Long id = administrare.notificariNerezolvate().get(0).getId();

        administrare.rezolvaNotificare(id);
        assertEquals(0, administrare.notificariNerezolvate().size());
    }

    @Test
    void inZiuaCuEvenimentSeFolosesteTarifulDeEveniment() {
        administrare.adaugaZiSpeciala(luni, "Concert");
        workflow.intrare(numar, luni.atTime(10, 0));

        // o ora la tarif de eveniment = 10 lei
        assertEquals(new BigDecimal("10.00"), workflow.sumaDePlata(numar, TipPerioada.ORA, luni.atTime(10, 40)));
    }

    @Test
    void administratorulPoateSchimbaTariful() {
        administrare.modificaTarif(TipZi.LUCRATOARE, new BigDecimal("6.00"), new BigDecimal("30.00"),
                new BigDecimal("120.00"), new BigDecimal("550.00"));
        workflow.intrare(numar, luni.atTime(10, 0));

        assertEquals(new BigDecimal("6.00"), workflow.sumaDePlata(numar, TipPerioada.ORA, luni.atTime(10, 40)));
    }

    @Test
    void tarifulNuPoateFiZero() {
        // regula V4
        assertThrows(IllegalArgumentException.class, () -> administrare.modificaTarif(TipZi.LUCRATOARE,
                BigDecimal.ZERO, new BigDecimal("30.00"), new BigDecimal("120.00"), new BigDecimal("550.00")));
    }

    @Test
    void raportulDeIncasari() {
        workflow.intrare(numar, luni.atTime(10, 0));
        workflow.plateste(numar, TipPerioada.ORA, MetodaPlata.CARD, CanalPlata.TONOMAT, true, luni.atTime(11, 10));

        assertEquals(new BigDecimal("10.00"), administrare.raportIncasari(luni));
    }
}

package ro.parcare.parcare_automatizate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.parcare.parcare_automatizate.enums.StareAbonament;
import ro.parcare.parcare_automatizate.enums.TipNotificare;
import ro.parcare.parcare_automatizate.factory.AbonamentFactoryImpl;
import ro.parcare.parcare_automatizate.factory.ParcareFactoryImpl;
import ro.parcare.parcare_automatizate.model.Abonament;
import ro.parcare.parcare_automatizate.model.NumarInmatriculare;
import ro.parcare.parcare_automatizate.model.Parcare;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AbonamentWorkflowServiceTest {

    private final LocalDate inceput = LocalDate.of(2026, 11, 1);

    private Parcare parcare;
    private NotificareService notificari;
    private AbonamentWorkflowService abonamente;

    @BeforeEach
    void pregateste() {
        // 2 locuri pentru abonati
        parcare = new ParcareFactoryImpl().creeazaParcare("Parcare Centru", "Str. Principala 1", 2, 2, 1, 70);
        notificari = new NotificareServiceImpl();
        abonamente = new AbonamentWorkflowServiceImpl(parcare, new AbonamentFactoryImpl(),
                new ValidareServiceImpl(), new CalculServiceImpl(), notificari, new AuditServiceImpl());
    }

    @Test
    void abonamentulNouEsteActivSiCosta500DeLei() {
        Abonament abonament = abonamente.cumparaAbonament(new NumarInmatriculare("IS 12 ABC"), true, inceput);

        assertEquals(StareAbonament.ACTIV, abonament.getStare());
        assertEquals(new BigDecimal("500.00"), abonament.getPerioade().get(0).getSuma());
    }

    @Test
    void doiAbonatiPrimescLocuriDiferite() {
        Abonament primul = abonamente.cumparaAbonament(new NumarInmatriculare("IS 11 AAA"), true, inceput);
        Abonament alDoilea = abonamente.cumparaAbonament(new NumarInmatriculare("IS 22 BBB"), true, inceput);

        assertNotEquals(primul.getIdLoc(), alDoilea.getIdLoc());
    }

    @Test
    void candLocurileSuntOcupateNuSeMaiVandAbonamente() {
        abonamente.cumparaAbonament(new NumarInmatriculare("IS 11 AAA"), true, inceput);
        abonamente.cumparaAbonament(new NumarInmatriculare("IS 22 BBB"), true, inceput);

        assertThrows(IllegalStateException.class,
                () -> abonamente.cumparaAbonament(new NumarInmatriculare("IS 33 CCC"), true, inceput));
    }

    @Test
    void reinnoireaReusitaAdaugaOLuna() {
        Abonament abonament = abonamente.cumparaAbonament(new NumarInmatriculare("IS 12 ABC"), false, inceput);
        assertTrue(abonamente.reinnoieste(abonament, true, LocalDate.of(2026, 11, 30).atTime(20, 0)));

        assertTrue(abonament.esteValabil(LocalDate.of(2026, 12, 15)));
    }

    @Test
    void reinnoireaEsuataTrimiteNotificare() {
        Abonament abonament = abonamente.cumparaAbonament(new NumarInmatriculare("IS 12 ABC"), false, inceput);
        assertFalse(abonamente.reinnoieste(abonament, false, LocalDate.of(2026, 11, 30).atTime(20, 0)));

        assertFalse(abonament.esteValabil(LocalDate.of(2026, 12, 15)));
        assertEquals(TipNotificare.PLATA_ESUATA, notificari.nerezolvate().get(0).getTip());
    }

    @Test
    void abonamentulFaraReinnoireExpira() {
        Abonament abonament = abonamente.cumparaAbonament(new NumarInmatriculare("IS 12 ABC"), false, inceput);
        abonamente.verificaExpirari(LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 1).atTime(0, 5));

        assertEquals(StareAbonament.EXPIRAT, abonament.getStare());
    }

    @Test
    void abonamentulCuReinnoireAutomataContinua() {
        Abonament abonament = abonamente.cumparaAbonament(new NumarInmatriculare("IS 12 ABC"), true, inceput);
        abonamente.verificaExpirari(LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 1).atTime(0, 5));

        assertEquals(StareAbonament.ACTIV, abonament.getStare());
        assertTrue(abonament.esteValabil(LocalDate.of(2026, 12, 1)));
    }
}

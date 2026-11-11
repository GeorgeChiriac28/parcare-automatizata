package ro.parcare.parcare_automatizate.service;

import org.junit.jupiter.api.Test;
import ro.parcare.parcare_automatizate.enums.CanalPlata;
import ro.parcare.parcare_automatizate.enums.MetodaPlata;
import ro.parcare.parcare_automatizate.enums.StareLoc;
import ro.parcare.parcare_automatizate.enums.StarePlata;
import ro.parcare.parcare_automatizate.enums.StareSesiune;
import ro.parcare.parcare_automatizate.factory.ParcareFactoryImpl;
import ro.parcare.parcare_automatizate.model.Abonament;
import ro.parcare.parcare_automatizate.model.LocParcare;
import ro.parcare.parcare_automatizate.model.NumarInmatriculare;
import ro.parcare.parcare_automatizate.model.Parcare;
import ro.parcare.parcare_automatizate.model.Plata;
import ro.parcare.parcare_automatizate.model.SesiuneParcare;
import ro.parcare.parcare_automatizate.model.Vehicul;
import ro.parcare.parcare_automatizate.model.Zona;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidareServiceTest {

    private final ValidareService validare = new ValidareServiceImpl();

    private final LocalDate azi = LocalDate.of(2026, 11, 9);
    private final NumarInmatriculare numar = new NumarInmatriculare("IS 12 ABC");

    // parcare mica: 2 locuri abonati (1, 2), 1 loc dizabilitati (3), 2 locuri ocazionali (4, 5)
    private Parcare creeazaParcare() {
        return new ParcareFactoryImpl().creeazaParcare("Parcare Centru", "Str. Lapusneanu 10", 2, 2, 1, 70);
    }

    private Zona zonaAbonati(Parcare parcare) {
        return parcare.getZone().get(0);
    }

    private Zona zonaOcazionali(Parcare parcare) {
        return parcare.getZone().get(1);
    }

    @Test
    void parcareaDinFactoryEsteCorecta() {
        // V2 si V3
        Parcare parcare = creeazaParcare();
        assertTrue(validare.capacitateCorecta(parcare));
        assertTrue(validare.numereLocuriUnice(parcare));
    }

    @Test
    void capacitateGresitaEsteDetectata() {
        // V2
        Parcare parcare = creeazaParcare();
        parcare.setCapacitate(100);
        assertFalse(validare.capacitateCorecta(parcare));
    }

    @Test
    void numereDuplicateSuntDetectate() {
        // V3
        Parcare parcare = creeazaParcare();
        zonaAbonati(parcare).getLocuri().get(1).setNumar(1);
        assertFalse(validare.numereLocuriUnice(parcare));
    }

    @Test
    void loculPentruDizabilitatiCereCard() {
        // A2: locul cu numarul 3 este pentru dizabilitati
        Parcare parcare = creeazaParcare();
        LocParcare locDizabilitati = zonaOcazionali(parcare).getLocuri().get(0);

        Vehicul faraCard = new Vehicul(numar, null);
        Vehicul cuCard = new Vehicul(numar, LocalDate.of(2027, 1, 1));

        assertFalse(validare.poateOcupaLoc(locDizabilitati, faraCard, azi));
        assertTrue(validare.poateOcupaLoc(locDizabilitati, cuCard, azi));
    }

    @Test
    void ocazionalNuIntraCandLocurileStandardSuntOcupate() {
        // A1: raman doar locuri pentru dizabilitati, iar soferul nu are card
        Parcare parcare = creeazaParcare();
        zonaOcazionali(parcare).getLocuri().get(1).setStare(StareLoc.OCUPAT);
        zonaOcazionali(parcare).getLocuri().get(2).setStare(StareLoc.OCUPAT);

        assertFalse(validare.existaLocLiberOcazional(parcare, null, azi));

        Vehicul cuCard = new Vehicul(numar, LocalDate.of(2027, 1, 1));
        assertTrue(validare.existaLocLiberOcazional(parcare, cuCard, azi));
    }

    @Test
    void nuSePotFaceMaiMulteAbonamenteDecatLocuri() {
        // A3: zona abonatilor are 2 locuri
        Parcare parcare = creeazaParcare();
        List<Abonament> abonamente = new ArrayList<>();
        abonamente.add(new Abonament(new NumarInmatriculare("IS 11 AAA"), 1L, true));
        assertTrue(validare.poateCreaAbonament(parcare, abonamente));

        abonamente.add(new Abonament(new NumarInmatriculare("IS 22 BBB"), 2L, true));
        assertFalse(validare.poateCreaAbonament(parcare, abonamente));
    }

    @Test
    void unLocRezervatNuPoateFiDatAltuiAbonat() {
        // A3
        List<Abonament> abonamente = new ArrayList<>();
        abonamente.add(new Abonament(new NumarInmatriculare("IS 11 AAA"), 1L, true));

        assertFalse(validare.locDisponibilPentruAbonament(1L, abonamente));
        assertTrue(validare.locDisponibilPentruAbonament(2L, abonamente));
    }

    @Test
    void oSesiuneInchisaNuMaiEsteActiva() {
        // A4
        List<SesiuneParcare> sesiuni = new ArrayList<>();
        SesiuneParcare sesiune = new SesiuneParcare(numar, azi.atTime(9, 0), 4L);
        sesiuni.add(sesiune);
        assertTrue(validare.areSesiuneActiva(numar, sesiuni));

        sesiune.setStare(StareSesiune.INCHISA);
        assertFalse(validare.areSesiuneActiva(numar, sesiuni));
    }

    @Test
    void celeCincisprezeceMinuteDeGratie() {
        // A5: platit pana la 12:00
        SesiuneParcare sesiune = new SesiuneParcare(numar, azi.atTime(10, 0), 4L);
        sesiune.setValabilPana(azi.atTime(12, 0));

        assertTrue(validare.poateIesiFaraTaxa(sesiune, azi.atTime(12, 10)));
        assertTrue(validare.poateIesiFaraTaxa(sesiune, azi.atTime(12, 15)));
        assertFalse(validare.poateIesiFaraTaxa(sesiune, azi.atTime(12, 16)));
    }

    @Test
    void faraPlataNuSePoateIesi() {
        // A5
        SesiuneParcare sesiune = new SesiuneParcare(numar, azi.atTime(10, 0), 4L);
        assertFalse(validare.poateIesiFaraTaxa(sesiune, azi.atTime(10, 5)));
    }

    @Test
    void plataEsuataNuPrelungestePerioada() {
        // A6
        Plata esuata = new Plata(new BigDecimal("10.00"), MetodaPlata.CARD, CanalPlata.TONOMAT,
                StarePlata.ESUATA, azi.atTime(11, 0));
        assertFalse(validare.plataPrelungestePerioada(esuata));
    }

    @Test
    void locRezervatOcupatDeAltcineva() {
        // A7: locul 1 e ocupat, dar abonatul nu are sesiune activa
        Parcare parcare = creeazaParcare();
        LocParcare locRezervat = zonaAbonati(parcare).getLocuri().get(0);
        locRezervat.setStare(StareLoc.OCUPAT);
        Abonament abonament = new Abonament(numar, locRezervat.getId(), true);

        assertTrue(validare.locOcupatIncorect(locRezervat, abonament, new ArrayList<>()));
    }

    @Test
    void abonatParcatPeAltLoc() {
        // A8: abonatul e in parcare, dar locul lui e liber
        Parcare parcare = creeazaParcare();
        LocParcare locRezervat = zonaAbonati(parcare).getLocuri().get(0);
        Abonament abonament = new Abonament(numar, locRezervat.getId(), true);

        List<SesiuneParcare> sesiuni = new ArrayList<>();
        sesiuni.add(new SesiuneParcare(numar, azi.atTime(9, 0), 4L));

        assertTrue(validare.abonatParcatPeAltLoc(locRezervat, abonament, sesiuni));
    }
}

package ro.parcare.parcare_automatizate.service;

import org.junit.jupiter.api.Test;
import ro.parcare.parcare_automatizate.enums.CanalPlata;
import ro.parcare.parcare_automatizate.enums.MetodaPlata;
import ro.parcare.parcare_automatizate.enums.StarePlata;
import ro.parcare.parcare_automatizate.enums.TipPerioada;
import ro.parcare.parcare_automatizate.enums.TipZi;
import ro.parcare.parcare_automatizate.factory.ParcareFactoryImpl;
import ro.parcare.parcare_automatizate.model.NumarInmatriculare;
import ro.parcare.parcare_automatizate.model.Parcare;
import ro.parcare.parcare_automatizate.model.Plata;
import ro.parcare.parcare_automatizate.model.SesiuneParcare;
import ro.parcare.parcare_automatizate.model.Tarif;
import ro.parcare.parcare_automatizate.model.ZiSpeciala;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculServiceTest {

    private final CalculService calcul = new CalculServiceImpl();

    // 9 noiembrie 2026 este luni, 14 noiembrie este sambata
    private final LocalDate luni = LocalDate.of(2026, 11, 9);
    private final LocalDate sambata = LocalDate.of(2026, 11, 14);

    private Parcare creeazaParcare() {
        return new ParcareFactoryImpl().creeazaParcare("Parcare Centru", "Str. Lapusneanu 10", 10, 8, 2, 70);
    }

    private Tarif tarifLucratoare() {
        Parcare parcare = creeazaParcare();
        return calcul.gasesteTarif(parcare, luni);
    }

    @Test
    void tipulZileiPentruLuniSambataSiEveniment() {
        List<ZiSpeciala> zileSpeciale = new ArrayList<>();
        zileSpeciale.add(new ZiSpeciala(LocalDate.of(2026, 11, 20), "Concert"));

        assertEquals(TipZi.LUCRATOARE, calcul.tipulZilei(luni, zileSpeciale));
        assertEquals(TipZi.WEEKEND, calcul.tipulZilei(sambata, zileSpeciale));
        assertEquals(TipZi.EVENIMENT, calcul.tipulZilei(LocalDate.of(2026, 11, 20), zileSpeciale));
    }

    @Test
    void tarifulDeWeekendEsteMaiMic() {
        Parcare parcare = creeazaParcare();
        Tarif tarif = calcul.gasesteTarif(parcare, sambata);
        assertEquals(new BigDecimal("4.00"), tarif.getPretOra());
    }

    @Test
    void oOraSi10MinuteInseamnaDouaOre() {
        // regula C1
        LocalDateTime intrare = luni.atTime(10, 0);
        LocalDateTime iesire = luni.atTime(11, 10);
        assertEquals(2, calcul.oreFacturate(intrare, iesire));
    }

    @Test
    void sumaPentruOOraSi10Minute() {
        // regula C2: 2 ore x 5 lei
        LocalDateTime intrare = luni.atTime(10, 0);
        LocalDateTime iesire = luni.atTime(11, 10);
        assertEquals(new BigDecimal("10.00"), calcul.sumaPentruOre(tarifLucratoare(), intrare, iesire));
    }

    @Test
    void sumaNuDepasestePretulPeZi() {
        // 15 ore x 5 lei = 75 lei, dar plafonul este 25 lei
        LocalDateTime intrare = luni.atTime(6, 0);
        LocalDateTime iesire = luni.atTime(21, 0);
        assertEquals(new BigDecimal("25.00"), calcul.sumaPentruOre(tarifLucratoare(), intrare, iesire));
    }

    @Test
    void sumaPentruMaiMultDeOZi() {
        // 26 de ore = o zi (25 lei) + 2 ore (2 x 5 = 10 lei)
        LocalDateTime intrare = luni.atTime(10, 0);
        LocalDateTime iesire = luni.plusDays(1).atTime(12, 0);
        assertEquals(new BigDecimal("35.00"), calcul.sumaPentruOre(tarifLucratoare(), intrare, iesire));
    }

    @Test
    void plataPeZiEsteValabila24DeOre() {
        LocalDateTime intrare = luni.atTime(10, 0);
        assertEquals(luni.plusDays(1).atTime(10, 0),
                calcul.valabilPana(TipPerioada.ZI, intrare, intrare.plusMinutes(5)));
    }

    @Test
    void plataPeOreEsteValabilaPanaLaSfarsitulOrei() {
        // a stat 1 h 10 min, a platit 2 ore, deci e valabil pana la 12:00
        LocalDateTime intrare = luni.atTime(10, 0);
        assertEquals(luni.atTime(12, 0),
                calcul.valabilPana(TipPerioada.ORA, intrare, luni.atTime(11, 10)));
    }

    @Test
    void taxaDeIntarziere() {
        // regula C4
        Tarif tarif = tarifLucratoare();
        assertEquals(new BigDecimal("25.00"), calcul.taxaIntarziere(tarif, TipPerioada.ORA));
        assertEquals(new BigDecimal("100.00"), calcul.taxaIntarziere(tarif, TipPerioada.ZI));
    }

    @Test
    void incasarileNuIncludPlatileEsuate() {
        // regula C7
        SesiuneParcare sesiune = new SesiuneParcare(new NumarInmatriculare("IS 12 ABC"), luni.atTime(9, 0), 1L);
        sesiune.adaugaPlata(new Plata(new BigDecimal("10.00"), MetodaPlata.CARD, CanalPlata.TONOMAT,
                StarePlata.REUSITA, luni.atTime(10, 0)));
        sesiune.adaugaPlata(new Plata(new BigDecimal("5.00"), MetodaPlata.CARD, CanalPlata.TONOMAT,
                StarePlata.ESUATA, luni.atTime(10, 5)));

        List<SesiuneParcare> sesiuni = new ArrayList<>();
        sesiuni.add(sesiune);

        assertEquals(new BigDecimal("10.00"), calcul.incasariPeZi(sesiuni, luni));
    }
}

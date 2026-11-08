package ro.parcare.parcare_automatizate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import ro.parcare.parcare_automatizate.enums.CanalPlata;
import ro.parcare.parcare_automatizate.enums.MetodaPlata;
import ro.parcare.parcare_automatizate.enums.StareLoc;
import ro.parcare.parcare_automatizate.enums.StarePlata;
import ro.parcare.parcare_automatizate.enums.TipLoc;
import ro.parcare.parcare_automatizate.enums.TipZona;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Teste pentru regulile de validare scrise ca adnotari in entitati
// si pentru metodele simple din entitati.
class EntitatiTest {

    // obiectul care verifica adnotarile (@NotNull, @Positive etc.)
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void parcareCorectaNuAreErori() {
        Parcare parcare = new Parcare("Parcare Centru", "Str. Lapusneanu 10", 20, 70);
        Set<ConstraintViolation<Parcare>> erori = validator.validate(parcare);
        assertTrue(erori.isEmpty());
    }

    @Test
    void parcareCuPragDeMurdariePeste100NuEsteValida() {
        // regula V8
        Parcare parcare = new Parcare("Parcare Centru", "Str. Lapusneanu 10", 20, 150);
        Set<ConstraintViolation<Parcare>> erori = validator.validate(parcare);
        assertEquals(1, erori.size());
    }

    @Test
    void parcareFaraLocuriNuEsteValida() {
        // regula V2
        Parcare parcare = new Parcare("Parcare Centru", "Str. Lapusneanu 10", 0, 70);
        Set<ConstraintViolation<Parcare>> erori = validator.validate(parcare);
        assertEquals(1, erori.size());
    }

    @Test
    void sesiuneCuIesireInainteDeIntrareNuEsteValida() {
        // regula V5
        SesiuneParcare sesiune = new SesiuneParcare(new NumarInmatriculare("IS 12 ABC"),
                LocalDateTime.of(2026, 11, 10, 12, 0), 1L);
        sesiune.setOraIesire(LocalDateTime.of(2026, 11, 10, 11, 0));
        Set<ConstraintViolation<SesiuneParcare>> erori = validator.validate(sesiune);
        assertEquals(1, erori.size());
    }

    @Test
    void plataCuSumaZeroNuEsteValida() {
        // regula V6
        Plata plata = new Plata(BigDecimal.ZERO, MetodaPlata.CARD, CanalPlata.TONOMAT,
                StarePlata.REUSITA, LocalDateTime.now());
        Set<ConstraintViolation<Plata>> erori = validator.validate(plata);
        assertEquals(1, erori.size());
    }

    @Test
    void perioadaMaiLungaDeOLunaNuEsteValida() {
        // regula V7
        PerioadaAbonament perioada = new PerioadaAbonament(LocalDate.of(2026, 11, 1),
                LocalDate.of(2026, 12, 15), new BigDecimal("500"), StarePlata.REUSITA);
        Set<ConstraintViolation<PerioadaAbonament>> erori = validator.validate(perioada);
        assertEquals(1, erori.size());
    }

    @Test
    void locuriLibereSeNumaraDoarInZonaCeruta() {
        // regula C5
        Parcare parcare = new Parcare("Parcare Centru", "Str. Lapusneanu 10", 3, 70);

        Zona ocazionali = new Zona("B", TipZona.OCAZIONALI, 2);
        ocazionali.adaugaLoc(new LocParcare(1, TipLoc.STANDARD));
        LocParcare locOcupat = new LocParcare(2, TipLoc.STANDARD);
        locOcupat.setStare(StareLoc.OCUPAT);
        ocazionali.adaugaLoc(locOcupat);

        Zona abonati = new Zona("A", TipZona.ABONATI, 1);
        abonati.adaugaLoc(new LocParcare(3, TipLoc.STANDARD));

        parcare.adaugaZona(ocazionali);
        parcare.adaugaZona(abonati);

        assertEquals(1, parcare.locuriLibere(TipZona.OCAZIONALI));
        assertEquals(1, parcare.locuriLibere(TipZona.ABONATI));
    }

    @Test
    void abonamentulEsteValabilDoarInPerioadaPlatita() {
        Abonament abonament = new Abonament(new NumarInmatriculare("IS 12 ABC"), 3L, true);
        abonament.adaugaPerioada(new PerioadaAbonament(LocalDate.of(2026, 11, 1),
                LocalDate.of(2026, 11, 30), new BigDecimal("500"), StarePlata.REUSITA));

        assertTrue(abonament.esteValabil(LocalDate.of(2026, 11, 15)));
        assertFalse(abonament.esteValabil(LocalDate.of(2026, 12, 5)));
    }
}

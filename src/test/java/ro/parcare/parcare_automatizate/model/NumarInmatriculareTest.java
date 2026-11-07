package ro.parcare.parcare_automatizate.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NumarInmatriculareTest {

    @Test
    void numarCorectDeJudet() {
        NumarInmatriculare numar = new NumarInmatriculare("IS 12 ABC");
        assertEquals("IS 12 ABC", numar.getValoare());
    }

    @Test
    void numarCorectDeBucuresti() {
        NumarInmatriculare numar = new NumarInmatriculare("B 123 XYZ");
        assertEquals("B 123 XYZ", numar.getValoare());
    }

    @Test
    void litereleMiciSuntTransformateInLitereMari() {
        NumarInmatriculare numar = new NumarInmatriculare("is 12 abc");
        assertEquals("IS 12 ABC", numar.getValoare());
    }

    @Test
    void douaNumereLaFelSuntEgale() {
        NumarInmatriculare numar1 = new NumarInmatriculare("IS 12 ABC");
        NumarInmatriculare numar2 = new NumarInmatriculare("IS 12 ABC");
        assertEquals(numar1, numar2);
    }

    @Test
    void numarGolNuEsteAcceptat() {
        assertThrows(IllegalArgumentException.class, () -> new NumarInmatriculare(""));
    }

    @Test
    void numarGresitNuEsteAcceptat() {
        assertThrows(IllegalArgumentException.class, () -> new NumarInmatriculare("IS 1 ABC"));
        assertThrows(IllegalArgumentException.class, () -> new NumarInmatriculare("ABC 12 IS"));
    }
}

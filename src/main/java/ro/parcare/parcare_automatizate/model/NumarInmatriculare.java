package ro.parcare.parcare_automatizate.model;

import java.util.Objects;

// Value object pentru numarul de inmatriculare (regula V1).
// Nu are id: doua numere cu acelasi text sunt considerate egale.
public class NumarInmatriculare {

    private final String valoare;

    public NumarInmatriculare(String valoare) {
        if (valoare == null || valoare.isBlank()) {
            throw new IllegalArgumentException("Numarul de inmatriculare este obligatoriu");
        }

        String text = valoare.trim().toUpperCase();

        // Bucuresti: B 12 ABC sau B 123 ABC
        boolean esteBucuresti = text.matches("B [0-9]{2,3} [A-Z]{3}");
        // alte judete: IS 12 ABC
        boolean esteJudet = text.matches("[A-Z]{2} [0-9]{2} [A-Z]{3}");

        if (!esteBucuresti && !esteJudet) {
            throw new IllegalArgumentException("Numar de inmatriculare invalid: " + valoare);
        }

        this.valoare = text;
    }

    public String getValoare() {
        return valoare;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NumarInmatriculare that = (NumarInmatriculare) o;
        return Objects.equals(valoare, that.valoare);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valoare);
    }

    @Override
    public String toString() {
        return valoare;
    }
}

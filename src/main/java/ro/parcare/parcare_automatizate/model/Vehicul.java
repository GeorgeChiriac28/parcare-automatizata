package ro.parcare.parcare_automatizate.model;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

// Entitatea root a agregatului Vehicul.
// Retine vehiculele care au card pentru persoane cu dizabilitati.
public class Vehicul {

    private Long id;

    @NotNull(message = "Numarul de inmatriculare este obligatoriu")
    private NumarInmatriculare nrAuto;

    // pana cand este valabil cardul (poate lipsi)
    private LocalDate cardDizabilitatiPana;

    public Vehicul() {
    }

    public Vehicul(NumarInmatriculare nrAuto, LocalDate cardDizabilitatiPana) {
        this.nrAuto = nrAuto;
        this.cardDizabilitatiPana = cardDizabilitatiPana;
    }

    // folosit la regula A2
    public boolean areCardValabil(LocalDate data) {
        if (cardDizabilitatiPana == null) {
            return false;
        }
        return !data.isAfter(cardDizabilitatiPana);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NumarInmatriculare getNrAuto() {
        return nrAuto;
    }

    public void setNrAuto(NumarInmatriculare nrAuto) {
        this.nrAuto = nrAuto;
    }

    public LocalDate getCardDizabilitatiPana() {
        return cardDizabilitatiPana;
    }

    public void setCardDizabilitatiPana(LocalDate cardDizabilitatiPana) {
        this.cardDizabilitatiPana = cardDizabilitatiPana;
    }
}

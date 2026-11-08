package ro.parcare.parcare_automatizate.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ro.parcare.parcare_automatizate.enums.TipZi;

import java.math.BigDecimal;

// Componenta a agregatului Parcare: preturile pentru un tip de zi.
// Folosim BigDecimal pentru bani, ca sa nu apara erori de rotunjire.
public class Tarif {

    private Long id;

    @NotNull(message = "Tipul zilei este obligatoriu")
    private TipZi tipZi;

    // regula V4: toate preturile sunt mai mari decat 0
    @NotNull
    @Positive(message = "Pretul pe ora trebuie sa fie mai mare decat 0")
    private BigDecimal pretOra;

    @NotNull
    @Positive(message = "Pretul pe zi trebuie sa fie mai mare decat 0")
    private BigDecimal pretZi;

    @NotNull
    @Positive(message = "Pretul pe saptamana trebuie sa fie mai mare decat 0")
    private BigDecimal pretSaptamana;

    @NotNull
    @Positive(message = "Pretul abonamentului trebuie sa fie mai mare decat 0")
    private BigDecimal pretAbonament;

    public Tarif() {
    }

    public Tarif(TipZi tipZi, BigDecimal pretOra, BigDecimal pretZi,
                 BigDecimal pretSaptamana, BigDecimal pretAbonament) {
        this.tipZi = tipZi;
        this.pretOra = pretOra;
        this.pretZi = pretZi;
        this.pretSaptamana = pretSaptamana;
        this.pretAbonament = pretAbonament;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipZi getTipZi() {
        return tipZi;
    }

    public void setTipZi(TipZi tipZi) {
        this.tipZi = tipZi;
    }

    public BigDecimal getPretOra() {
        return pretOra;
    }

    public void setPretOra(BigDecimal pretOra) {
        this.pretOra = pretOra;
    }

    public BigDecimal getPretZi() {
        return pretZi;
    }

    public void setPretZi(BigDecimal pretZi) {
        this.pretZi = pretZi;
    }

    public BigDecimal getPretSaptamana() {
        return pretSaptamana;
    }

    public void setPretSaptamana(BigDecimal pretSaptamana) {
        this.pretSaptamana = pretSaptamana;
    }

    public BigDecimal getPretAbonament() {
        return pretAbonament;
    }

    public void setPretAbonament(BigDecimal pretAbonament) {
        this.pretAbonament = pretAbonament;
    }
}

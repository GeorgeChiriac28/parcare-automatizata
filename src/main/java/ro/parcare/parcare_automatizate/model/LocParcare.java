package ro.parcare.parcare_automatizate.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ro.parcare.parcare_automatizate.enums.StareLoc;
import ro.parcare.parcare_automatizate.enums.TipLoc;

// Componenta a agregatului Parcare (se afla intr-o zona).
public class LocParcare {

    private Long id;

    // regula V3: numarul trebuie sa fie unic in parcare (se verifica in serviciul de validare)
    @Positive(message = "Numarul locului trebuie sa fie mai mare decat 0")
    private int numar;

    @NotNull(message = "Tipul locului este obligatoriu")
    private TipLoc tip;

    @NotNull(message = "Starea locului este obligatorie")
    private StareLoc stare;

    public LocParcare() {
    }

    public LocParcare(int numar, TipLoc tip) {
        this.numar = numar;
        this.tip = tip;
        this.stare = StareLoc.LIBER;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNumar() {
        return numar;
    }

    public void setNumar(int numar) {
        this.numar = numar;
    }

    public TipLoc getTip() {
        return tip;
    }

    public void setTip(TipLoc tip) {
        this.tip = tip;
    }

    public StareLoc getStare() {
        return stare;
    }

    public void setStare(StareLoc stare) {
        this.stare = stare;
    }
}

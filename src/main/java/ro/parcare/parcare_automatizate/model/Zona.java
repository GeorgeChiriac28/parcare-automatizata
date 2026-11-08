package ro.parcare.parcare_automatizate.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ro.parcare.parcare_automatizate.enums.TipZona;

import java.util.ArrayList;
import java.util.List;

// Componenta a agregatului Parcare.
public class Zona {

    private Long id;

    @NotBlank(message = "Codul zonei este obligatoriu")
    private String cod;

    @NotNull(message = "Tipul zonei este obligatoriu")
    private TipZona tip;

    @Positive(message = "Zona trebuie sa aiba cel putin un loc")
    private int nrLocuri;

    @Valid
    private List<LocParcare> locuri = new ArrayList<>();

    public Zona() {
    }

    public Zona(String cod, TipZona tip, int nrLocuri) {
        this.cod = cod;
        this.tip = tip;
        this.nrLocuri = nrLocuri;
    }

    public void adaugaLoc(LocParcare loc) {
        locuri.add(loc);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public TipZona getTip() {
        return tip;
    }

    public void setTip(TipZona tip) {
        this.tip = tip;
    }

    public int getNrLocuri() {
        return nrLocuri;
    }

    public void setNrLocuri(int nrLocuri) {
        this.nrLocuri = nrLocuri;
    }

    public List<LocParcare> getLocuri() {
        return locuri;
    }
}

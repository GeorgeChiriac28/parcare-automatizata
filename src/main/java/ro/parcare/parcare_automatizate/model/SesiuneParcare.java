package ro.parcare.parcare_automatizate.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import ro.parcare.parcare_automatizate.enums.StareSesiune;
import ro.parcare.parcare_automatizate.enums.TipPerioada;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Entitatea root a agregatului SesiuneParcare.
// O sesiune incepe cand masina intra si se inchide cand iese.
public class SesiuneParcare {

    private Long id;

    @NotNull(message = "Numarul de inmatriculare este obligatoriu")
    private NumarInmatriculare nrAuto;

    @NotNull(message = "Ora intrarii este obligatorie")
    private LocalDateTime oraIntrare;

    // pana cand este platita parcarea (se completeaza dupa plata)
    private LocalDateTime valabilPana;

    private LocalDateTime oraIesire;

    private TipPerioada tipPerioada;

    @NotNull(message = "Starea sesiunii este obligatorie")
    private StareSesiune stare;

    // locul apartine altui agregat (Parcare), asa ca tinem doar id-ul lui
    private Long idLoc;

    @Valid
    private List<Plata> plati = new ArrayList<>();

    // pentru audit
    private LocalDateTime dataCreare;
    private LocalDateTime dataModificare;

    public SesiuneParcare() {
    }

    public SesiuneParcare(NumarInmatriculare nrAuto, LocalDateTime oraIntrare, Long idLoc) {
        this.nrAuto = nrAuto;
        this.oraIntrare = oraIntrare;
        this.idLoc = idLoc;
        this.stare = StareSesiune.ACTIVA;
    }

    public void adaugaPlata(Plata plata) {
        plati.add(plata);
    }

    // regula V5: ora iesirii, daca exista, trebuie sa fie dupa ora intrarii
    @AssertTrue(message = "Ora iesirii trebuie sa fie dupa ora intrarii")
    public boolean isOraIesireCorecta() {
        if (oraIesire == null || oraIntrare == null) {
            return true;
        }
        return oraIesire.isAfter(oraIntrare);
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

    public LocalDateTime getOraIntrare() {
        return oraIntrare;
    }

    public void setOraIntrare(LocalDateTime oraIntrare) {
        this.oraIntrare = oraIntrare;
    }

    public LocalDateTime getValabilPana() {
        return valabilPana;
    }

    public void setValabilPana(LocalDateTime valabilPana) {
        this.valabilPana = valabilPana;
    }

    public LocalDateTime getOraIesire() {
        return oraIesire;
    }

    public void setOraIesire(LocalDateTime oraIesire) {
        this.oraIesire = oraIesire;
    }

    public TipPerioada getTipPerioada() {
        return tipPerioada;
    }

    public void setTipPerioada(TipPerioada tipPerioada) {
        this.tipPerioada = tipPerioada;
    }

    public StareSesiune getStare() {
        return stare;
    }

    public void setStare(StareSesiune stare) {
        this.stare = stare;
    }

    public Long getIdLoc() {
        return idLoc;
    }

    public void setIdLoc(Long idLoc) {
        this.idLoc = idLoc;
    }

    public List<Plata> getPlati() {
        return plati;
    }

    public LocalDateTime getDataCreare() {
        return dataCreare;
    }

    public void setDataCreare(LocalDateTime dataCreare) {
        this.dataCreare = dataCreare;
    }

    public LocalDateTime getDataModificare() {
        return dataModificare;
    }

    public void setDataModificare(LocalDateTime dataModificare) {
        this.dataModificare = dataModificare;
    }
}

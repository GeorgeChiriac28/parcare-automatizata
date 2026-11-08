package ro.parcare.parcare_automatizate.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import ro.parcare.parcare_automatizate.enums.StareAbonament;
import ro.parcare.parcare_automatizate.enums.StarePlata;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Entitatea root a agregatului Abonament.
// Fiecare luna platita este o PerioadaAbonament.
public class Abonament {

    private Long id;

    @NotNull(message = "Numarul de inmatriculare este obligatoriu")
    private NumarInmatriculare nrAuto;

    // locul rezervat; apartine agregatului Parcare, asa ca tinem doar id-ul
    @NotNull(message = "Abonamentul trebuie sa aiba un loc rezervat")
    private Long idLoc;

    private boolean reinnoireAutomata;

    @NotNull(message = "Starea abonamentului este obligatorie")
    private StareAbonament stare;

    @Valid
    private List<PerioadaAbonament> perioade = new ArrayList<>();

    // pentru audit
    private LocalDateTime dataCreare;
    private LocalDateTime dataModificare;

    public Abonament() {
    }

    public Abonament(NumarInmatriculare nrAuto, Long idLoc, boolean reinnoireAutomata) {
        this.nrAuto = nrAuto;
        this.idLoc = idLoc;
        this.reinnoireAutomata = reinnoireAutomata;
        this.stare = StareAbonament.ACTIV;
    }

    public void adaugaPerioada(PerioadaAbonament perioada) {
        perioade.add(perioada);
    }

    // abonamentul este valabil la o data daca e activ si are o perioada platita care contine data
    public boolean esteValabil(LocalDate data) {
        if (stare != StareAbonament.ACTIV) {
            return false;
        }
        for (PerioadaAbonament perioada : perioade) {
            if (perioada.getStare() == StarePlata.REUSITA && perioada.contineData(data)) {
                return true;
            }
        }
        return false;
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

    public Long getIdLoc() {
        return idLoc;
    }

    public void setIdLoc(Long idLoc) {
        this.idLoc = idLoc;
    }

    public boolean isReinnoireAutomata() {
        return reinnoireAutomata;
    }

    public void setReinnoireAutomata(boolean reinnoireAutomata) {
        this.reinnoireAutomata = reinnoireAutomata;
    }

    public StareAbonament getStare() {
        return stare;
    }

    public void setStare(StareAbonament stare) {
        this.stare = stare;
    }

    public List<PerioadaAbonament> getPerioade() {
        return perioade;
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

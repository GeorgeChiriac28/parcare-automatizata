package ro.parcare.parcare_automatizate.model;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ro.parcare.parcare_automatizate.enums.StarePlata;

import java.math.BigDecimal;
import java.time.LocalDate;

// Componenta a agregatului Abonament: o luna platita.
public class PerioadaAbonament {

    private Long id;

    @NotNull(message = "Data de inceput este obligatorie")
    private LocalDate dataInceput;

    @NotNull(message = "Data de sfarsit este obligatorie")
    private LocalDate dataSfarsit;

    @NotNull
    @Positive(message = "Suma trebuie sa fie mai mare decat 0")
    private BigDecimal suma;

    @NotNull(message = "Starea platii este obligatorie")
    private StarePlata stare;

    public PerioadaAbonament() {
    }

    public PerioadaAbonament(LocalDate dataInceput, LocalDate dataSfarsit, BigDecimal suma, StarePlata stare) {
        this.dataInceput = dataInceput;
        this.dataSfarsit = dataSfarsit;
        this.suma = suma;
        this.stare = stare;
    }

    // regula V7: perioada dureaza cel mult o luna si se termina dupa ce incepe
    @AssertTrue(message = "Perioada trebuie sa dureze cel mult o luna")
    public boolean isDurataCorecta() {
        if (dataInceput == null || dataSfarsit == null) {
            return true;
        }
        boolean dupaInceput = dataSfarsit.isAfter(dataInceput);
        boolean celMultOLuna = !dataSfarsit.isAfter(dataInceput.plusMonths(1));
        return dupaInceput && celMultOLuna;
    }

    // verifica daca o data se afla in aceasta perioada (inclusiv capetele)
    public boolean contineData(LocalDate data) {
        return !data.isBefore(dataInceput) && !data.isAfter(dataSfarsit);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataInceput() {
        return dataInceput;
    }

    public void setDataInceput(LocalDate dataInceput) {
        this.dataInceput = dataInceput;
    }

    public LocalDate getDataSfarsit() {
        return dataSfarsit;
    }

    public void setDataSfarsit(LocalDate dataSfarsit) {
        this.dataSfarsit = dataSfarsit;
    }

    public BigDecimal getSuma() {
        return suma;
    }

    public void setSuma(BigDecimal suma) {
        this.suma = suma;
    }

    public StarePlata getStare() {
        return stare;
    }

    public void setStare(StarePlata stare) {
        this.stare = stare;
    }
}

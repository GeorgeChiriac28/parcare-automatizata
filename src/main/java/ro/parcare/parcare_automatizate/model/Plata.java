package ro.parcare.parcare_automatizate.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ro.parcare.parcare_automatizate.enums.CanalPlata;
import ro.parcare.parcare_automatizate.enums.MetodaPlata;
import ro.parcare.parcare_automatizate.enums.StarePlata;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Componenta a agregatului SesiuneParcare.
public class Plata {

    private Long id;

    // regula V6: suma este mai mare decat 0
    @NotNull
    @Positive(message = "Suma trebuie sa fie mai mare decat 0")
    private BigDecimal suma;

    @NotNull(message = "Metoda de plata este obligatorie")
    private MetodaPlata metoda;

    @NotNull(message = "Canalul de plata este obligatoriu")
    private CanalPlata canal;

    @NotNull(message = "Starea platii este obligatorie")
    private StarePlata stare;

    @NotNull(message = "Data platii este obligatorie")
    private LocalDateTime data;

    public Plata() {
    }

    public Plata(BigDecimal suma, MetodaPlata metoda, CanalPlata canal, StarePlata stare, LocalDateTime data) {
        this.suma = suma;
        this.metoda = metoda;
        this.canal = canal;
        this.stare = stare;
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getSuma() {
        return suma;
    }

    public void setSuma(BigDecimal suma) {
        this.suma = suma;
    }

    public MetodaPlata getMetoda() {
        return metoda;
    }

    public void setMetoda(MetodaPlata metoda) {
        this.metoda = metoda;
    }

    public CanalPlata getCanal() {
        return canal;
    }

    public void setCanal(CanalPlata canal) {
        this.canal = canal;
    }

    public StarePlata getStare() {
        return stare;
    }

    public void setStare(StarePlata stare) {
        this.stare = stare;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }
}

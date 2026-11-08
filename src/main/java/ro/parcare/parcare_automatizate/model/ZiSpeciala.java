package ro.parcare.parcare_automatizate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

// Componenta a agregatului Parcare: o zi cu tarif de eveniment (festival, concert etc.).
public class ZiSpeciala {

    private Long id;

    @NotNull(message = "Data este obligatorie")
    private LocalDate data;

    @NotBlank(message = "Denumirea evenimentului este obligatorie")
    private String denumire;

    public ZiSpeciala() {
    }

    public ZiSpeciala(LocalDate data, String denumire) {
        this.data = data;
        this.denumire = denumire;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getDenumire() {
        return denumire;
    }

    public void setDenumire(String denumire) {
        this.denumire = denumire;
    }
}

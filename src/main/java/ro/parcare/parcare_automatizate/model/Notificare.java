package ro.parcare.parcare_automatizate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ro.parcare.parcare_automatizate.enums.TipNotificare;

import java.time.LocalDateTime;

// Entitatea root a agregatului Notificare: un mesaj pentru administrator.
public class Notificare {

    private Long id;

    @NotNull(message = "Tipul notificarii este obligatoriu")
    private TipNotificare tip;

    @NotBlank(message = "Mesajul este obligatoriu")
    private String mesaj;

    @NotNull(message = "Data este obligatorie")
    private LocalDateTime data;

    private boolean rezolvata;

    public Notificare() {
    }

    public Notificare(TipNotificare tip, String mesaj, LocalDateTime data) {
        this.tip = tip;
        this.mesaj = mesaj;
        this.data = data;
        this.rezolvata = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipNotificare getTip() {
        return tip;
    }

    public void setTip(TipNotificare tip) {
        this.tip = tip;
    }

    public String getMesaj() {
        return mesaj;
    }

    public void setMesaj(String mesaj) {
        this.mesaj = mesaj;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public boolean isRezolvata() {
        return rezolvata;
    }

    public void setRezolvata(boolean rezolvata) {
        this.rezolvata = rezolvata;
    }
}

package ro.parcare.parcare_automatizate.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import ro.parcare.parcare_automatizate.enums.StareLoc;
import ro.parcare.parcare_automatizate.enums.TipZona;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Entitatea root a agregatului Parcare.
// Zonele, tarifele si zilele speciale se adauga doar prin parcare.
public class Parcare implements Auditabil {

    private Long id;

    @NotBlank(message = "Numele parcarii este obligatoriu")
    private String nume;

    @NotBlank(message = "Adresa este obligatorie")
    private String adresa;

    // regula V2 (prima parte): capacitatea trebuie sa fie mai mare decat 0
    @Positive(message = "Capacitatea trebuie sa fie mai mare decat 0")
    private int capacitate;

    // regula V8: pragul de murdarie este intre 0 si 100
    @Min(value = 0, message = "Pragul de murdarie nu poate fi mai mic decat 0")
    @Max(value = 100, message = "Pragul de murdarie nu poate fi mai mare decat 100")
    private int pragMurdarie;

    @Valid
    private List<Zona> zone = new ArrayList<>();

    @Valid
    private List<Tarif> tarife = new ArrayList<>();

    @Valid
    private List<ZiSpeciala> zileSpeciale = new ArrayList<>();

    // pentru audit
    private LocalDateTime dataCreare;
    private LocalDateTime dataModificare;

    public Parcare() {
    }

    public Parcare(String nume, String adresa, int capacitate, int pragMurdarie) {
        this.nume = nume;
        this.adresa = adresa;
        this.capacitate = capacitate;
        this.pragMurdarie = pragMurdarie;
    }

    public void adaugaZona(Zona zona) {
        zone.add(zona);
    }

    public void adaugaTarif(Tarif tarif) {
        tarife.add(tarif);
    }

    public void adaugaZiSpeciala(ZiSpeciala ziSpeciala) {
        zileSpeciale.add(ziSpeciala);
    }

    // cauta un loc dupa id; intoarce null daca nu exista
    public LocParcare gasesteLoc(Long idLoc) {
        for (Zona zona : zone) {
            for (LocParcare loc : zona.getLocuri()) {
                if (loc.getId().equals(idLoc)) {
                    return loc;
                }
            }
        }
        return null;
    }

    // cauta un loc dupa numarul scris pe el; intoarce null daca nu exista
    public LocParcare gasesteLocDupaNumar(int numar) {
        for (Zona zona : zone) {
            for (LocParcare loc : zona.getLocuri()) {
                if (loc.getNumar() == numar) {
                    return loc;
                }
            }
        }
        return null;
    }

    // view calculat (regula C5): cate locuri libere sunt intr-un tip de zona
    public int locuriLibere(TipZona tipZona) {
        int libere = 0;
        for (Zona zona : zone) {
            if (zona.getTip() == tipZona) {
                for (LocParcare loc : zona.getLocuri()) {
                    if (loc.getStare() == StareLoc.LIBER) {
                        libere++;
                    }
                }
            }
        }
        return libere;
    }

    // view calculat (regula C6): gradul de ocupare in procente
    public double gradOcupare() {
        if (capacitate == 0) {
            return 0;
        }
        int ocupate = 0;
        for (Zona zona : zone) {
            for (LocParcare loc : zona.getLocuri()) {
                if (loc.getStare() == StareLoc.OCUPAT) {
                    ocupate++;
                }
            }
        }
        return ocupate * 100.0 / capacitate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public int getCapacitate() {
        return capacitate;
    }

    public void setCapacitate(int capacitate) {
        this.capacitate = capacitate;
    }

    public int getPragMurdarie() {
        return pragMurdarie;
    }

    public void setPragMurdarie(int pragMurdarie) {
        this.pragMurdarie = pragMurdarie;
    }

    public List<Zona> getZone() {
        return zone;
    }

    public List<Tarif> getTarife() {
        return tarife;
    }

    public List<ZiSpeciala> getZileSpeciale() {
        return zileSpeciale;
    }

    public LocalDateTime getDataCreare() {
        return dataCreare;
    }

    @Override
    public void setDataCreare(LocalDateTime dataCreare) {
        this.dataCreare = dataCreare;
    }

    public LocalDateTime getDataModificare() {
        return dataModificare;
    }

    @Override
    public void setDataModificare(LocalDateTime dataModificare) {
        this.dataModificare = dataModificare;
    }
}

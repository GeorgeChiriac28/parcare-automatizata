package ro.parcare.parcare_automatizate.factory;

import org.springframework.stereotype.Component;
import ro.parcare.parcare_automatizate.enums.TipLoc;
import ro.parcare.parcare_automatizate.enums.TipZi;
import ro.parcare.parcare_automatizate.enums.TipZona;
import ro.parcare.parcare_automatizate.model.LocParcare;
import ro.parcare.parcare_automatizate.model.Parcare;
import ro.parcare.parcare_automatizate.model.Tarif;
import ro.parcare.parcare_automatizate.model.Zona;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class ParcareFactoryImpl implements ParcareFactory {

    // pana in Sprint 3 nu avem baza de date, asa ca dam noi id-urile
    private long urmatorulIdParcare = 1;
    private long urmatorulIdLoc = 1;

    @Override
    public Parcare creeazaParcare(String nume, String adresa,
                                  int locuriAbonati, int locuriOcazionali, int locuriDizabilitati,
                                  int pragMurdarie) {

        // locurile pentru dizabilitati sunt in zona ocazionalilor
        int capacitate = locuriAbonati + locuriOcazionali + locuriDizabilitati;

        Parcare parcare = new Parcare(nume, adresa, capacitate, pragMurdarie);
        parcare.setId(urmatorulIdParcare);
        urmatorulIdParcare++;

        int numarLoc = 1;

        // zona A: abonati, toate locurile standard
        Zona zonaAbonati = new Zona("A", TipZona.ABONATI, locuriAbonati);
        for (int i = 0; i < locuriAbonati; i++) {
            zonaAbonati.adaugaLoc(creeazaLoc(numarLoc, TipLoc.STANDARD));
            numarLoc++;
        }

        // zona B: ocazionali, mai intai locurile pentru dizabilitati, apoi cele standard
        Zona zonaOcazionali = new Zona("B", TipZona.OCAZIONALI, locuriOcazionali + locuriDizabilitati);
        for (int i = 0; i < locuriDizabilitati; i++) {
            zonaOcazionali.adaugaLoc(creeazaLoc(numarLoc, TipLoc.DIZABILITATI));
            numarLoc++;
        }
        for (int i = 0; i < locuriOcazionali; i++) {
            zonaOcazionali.adaugaLoc(creeazaLoc(numarLoc, TipLoc.STANDARD));
            numarLoc++;
        }

        parcare.adaugaZona(zonaAbonati);
        parcare.adaugaZona(zonaOcazionali);

        // tarifele de referinta din documentul Sprint 1 (sectiunea 6.5)
        parcare.adaugaTarif(new Tarif(TipZi.LUCRATOARE, new BigDecimal("5.00"), new BigDecimal("25.00"),
                new BigDecimal("100.00"), new BigDecimal("500.00")));
        parcare.adaugaTarif(new Tarif(TipZi.WEEKEND, new BigDecimal("4.00"), new BigDecimal("20.00"),
                new BigDecimal("100.00"), new BigDecimal("500.00")));
        parcare.adaugaTarif(new Tarif(TipZi.EVENIMENT, new BigDecimal("10.00"), new BigDecimal("50.00"),
                new BigDecimal("100.00"), new BigDecimal("500.00")));

        parcare.setDataCreare(LocalDateTime.now());
        return parcare;
    }

    private LocParcare creeazaLoc(int numar, TipLoc tip) {
        LocParcare loc = new LocParcare(numar, tip);
        loc.setId(urmatorulIdLoc);
        urmatorulIdLoc++;
        return loc;
    }
}

package ro.parcare.parcare_automatizate.service;

import org.springframework.stereotype.Service;
import ro.parcare.parcare_automatizate.enums.StareAbonament;
import ro.parcare.parcare_automatizate.enums.StareLoc;
import ro.parcare.parcare_automatizate.enums.StarePlata;
import ro.parcare.parcare_automatizate.enums.StareSesiune;
import ro.parcare.parcare_automatizate.enums.TipLoc;
import ro.parcare.parcare_automatizate.enums.TipZona;
import ro.parcare.parcare_automatizate.model.Abonament;
import ro.parcare.parcare_automatizate.model.LocParcare;
import ro.parcare.parcare_automatizate.model.NumarInmatriculare;
import ro.parcare.parcare_automatizate.model.Parcare;
import ro.parcare.parcare_automatizate.model.Plata;
import ro.parcare.parcare_automatizate.model.SesiuneParcare;
import ro.parcare.parcare_automatizate.model.Vehicul;
import ro.parcare.parcare_automatizate.model.Zona;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ValidareServiceImpl implements ValidareService {

    private static final int MINUTE_GRATIE = 15;

    @Override
    public boolean capacitateCorecta(Parcare parcare) {
        int totalLocuri = 0;
        for (Zona zona : parcare.getZone()) {
            totalLocuri = totalLocuri + zona.getLocuri().size();
        }
        return totalLocuri == parcare.getCapacitate();
    }

    @Override
    public boolean numereLocuriUnice(Parcare parcare) {
        List<Integer> numereVazute = new ArrayList<>();
        for (Zona zona : parcare.getZone()) {
            for (LocParcare loc : zona.getLocuri()) {
                if (numereVazute.contains(loc.getNumar())) {
                    return false;
                }
                numereVazute.add(loc.getNumar());
            }
        }
        return true;
    }

    @Override
    public boolean poateOcupaLoc(LocParcare loc, Vehicul vehicul, LocalDate data) {
        if (loc.getStare() != StareLoc.LIBER) {
            return false;
        }
        if (loc.getTip() == TipLoc.STANDARD) {
            return true;
        }
        // loc pentru dizabilitati: trebuie card valabil
        return vehicul != null && vehicul.areCardValabil(data);
    }

    @Override
    public boolean existaLocLiberOcazional(Parcare parcare, Vehicul vehicul, LocalDate data) {
        for (Zona zona : parcare.getZone()) {
            if (zona.getTip() == TipZona.OCAZIONALI) {
                for (LocParcare loc : zona.getLocuri()) {
                    if (poateOcupaLoc(loc, vehicul, data)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean poateCreaAbonament(Parcare parcare, List<Abonament> abonamente) {
        int locuriAbonati = 0;
        for (Zona zona : parcare.getZone()) {
            if (zona.getTip() == TipZona.ABONATI) {
                locuriAbonati = locuriAbonati + zona.getLocuri().size();
            }
        }

        int abonamenteActive = 0;
        for (Abonament abonament : abonamente) {
            if (abonament.getStare() == StareAbonament.ACTIV) {
                abonamenteActive++;
            }
        }
        return abonamenteActive < locuriAbonati;
    }

    @Override
    public boolean locDisponibilPentruAbonament(Long idLoc, List<Abonament> abonamente) {
        for (Abonament abonament : abonamente) {
            if (abonament.getStare() == StareAbonament.ACTIV && abonament.getIdLoc().equals(idLoc)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean areSesiuneActiva(NumarInmatriculare nrAuto, List<SesiuneParcare> sesiuni) {
        for (SesiuneParcare sesiune : sesiuni) {
            if (sesiune.getStare() == StareSesiune.ACTIVA && sesiune.getNrAuto().equals(nrAuto)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean poateIesiFaraTaxa(SesiuneParcare sesiune, LocalDateTime oraIesire) {
        // daca nu a platit nimic, nu poate iesi
        if (sesiune.getValabilPana() == null) {
            return false;
        }
        LocalDateTime limita = sesiune.getValabilPana().plusMinutes(MINUTE_GRATIE);
        return !oraIesire.isAfter(limita);
    }

    @Override
    public boolean plataPrelungestePerioada(Plata plata) {
        return plata.getStare() == StarePlata.REUSITA;
    }

    @Override
    public boolean locOcupatIncorect(LocParcare locRezervat, Abonament abonament, List<SesiuneParcare> sesiuni) {
        boolean locOcupat = locRezervat.getStare() == StareLoc.OCUPAT;
        boolean abonatInParcare = areSesiuneActiva(abonament.getNrAuto(), sesiuni);
        return locOcupat && !abonatInParcare;
    }

    @Override
    public boolean abonatParcatPeAltLoc(LocParcare locRezervat, Abonament abonament, List<SesiuneParcare> sesiuni) {
        boolean locLiber = locRezervat.getStare() == StareLoc.LIBER;
        boolean abonatInParcare = areSesiuneActiva(abonament.getNrAuto(), sesiuni);
        return locLiber && abonatInParcare;
    }
}

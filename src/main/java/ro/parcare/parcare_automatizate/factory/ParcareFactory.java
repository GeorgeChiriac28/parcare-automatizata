package ro.parcare.parcare_automatizate.factory;

import ro.parcare.parcare_automatizate.model.Parcare;

// Construieste agregatul Parcare: parcarea, zonele, locurile si tarifele.
public interface ParcareFactory {

    Parcare creeazaParcare(String nume, String adresa,
                           int locuriAbonati, int locuriOcazionali, int locuriDizabilitati,
                           int pragMurdarie);
}

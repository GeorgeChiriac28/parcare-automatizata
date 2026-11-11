package ro.parcare.parcare_automatizate.service;

import ro.parcare.parcare_automatizate.enums.CanalPlata;
import ro.parcare.parcare_automatizate.enums.MetodaPlata;
import ro.parcare.parcare_automatizate.enums.TipPerioada;
import ro.parcare.parcare_automatizate.enums.TipZona;
import ro.parcare.parcare_automatizate.model.NumarInmatriculare;
import ro.parcare.parcare_automatizate.model.Plata;
import ro.parcare.parcare_automatizate.model.SesiuneParcare;
import ro.parcare.parcare_automatizate.model.Vehicul;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// Fluxul principal: UC1 intrare, UC2 plata, UC3 iesire, UC4 locuri libere.
public interface ParcareWorkflowService {

    // retine un vehicul cu card pentru persoane cu dizabilitati
    void inregistreazaVehicul(Vehicul vehicul);

    // UC1: camera de la intrare a citit numarul
    SesiuneParcare intrare(NumarInmatriculare nrAuto, LocalDateTime ora);

    // UC2: cat are de plata soferul (afisat la tonomat sau in aplicatie)
    BigDecimal sumaDePlata(NumarInmatriculare nrAuto, TipPerioada tipPerioada, LocalDateTime acum);

    // UC2: plata; plataReusita spune daca tonomatul a reusit sa incaseze banii
    Plata plateste(NumarInmatriculare nrAuto, TipPerioada tipPerioada, MetodaPlata metoda,
                   CanalPlata canal, boolean plataReusita, LocalDateTime acum);

    // UC3: camera de la iesire a citit numarul; intoarce true daca bariera se deschide
    boolean iesire(NumarInmatriculare nrAuto, LocalDateTime ora);

    // UC4: locurile libere dintr-o zona
    int locuriLibere(TipZona tipZona);

    // sesiunea activa pentru un numar; null daca masina nu este in parcare
    SesiuneParcare gasesteSesiuneActiva(NumarInmatriculare nrAuto);

    List<SesiuneParcare> getSesiuni();
}

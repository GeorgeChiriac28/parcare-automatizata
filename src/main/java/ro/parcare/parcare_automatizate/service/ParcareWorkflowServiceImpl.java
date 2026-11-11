package ro.parcare.parcare_automatizate.service;

import org.springframework.stereotype.Service;
import ro.parcare.parcare_automatizate.enums.CanalPlata;
import ro.parcare.parcare_automatizate.enums.MetodaPlata;
import ro.parcare.parcare_automatizate.enums.StareLoc;
import ro.parcare.parcare_automatizate.enums.StarePlata;
import ro.parcare.parcare_automatizate.enums.StareSesiune;
import ro.parcare.parcare_automatizate.enums.TipNotificare;
import ro.parcare.parcare_automatizate.enums.TipPerioada;
import ro.parcare.parcare_automatizate.enums.TipZona;
import ro.parcare.parcare_automatizate.factory.SesiuneFactory;
import ro.parcare.parcare_automatizate.model.Abonament;
import ro.parcare.parcare_automatizate.model.LocParcare;
import ro.parcare.parcare_automatizate.model.NumarInmatriculare;
import ro.parcare.parcare_automatizate.model.Parcare;
import ro.parcare.parcare_automatizate.model.Plata;
import ro.parcare.parcare_automatizate.model.SesiuneParcare;
import ro.parcare.parcare_automatizate.model.Tarif;
import ro.parcare.parcare_automatizate.model.Vehicul;
import ro.parcare.parcare_automatizate.model.Zona;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ParcareWorkflowServiceImpl implements ParcareWorkflowService {

    private final Parcare parcare;
    private final SesiuneFactory sesiuneFactory;
    private final CalculService calcul;
    private final ValidareService validare;
    private final AbonamentWorkflowService abonamente;
    private final NotificareService notificari;
    private final AuditService audit;

    // pana in Sprint 3 tinem datele in liste in memorie
    private final List<SesiuneParcare> sesiuni = new ArrayList<>();
    private final List<Vehicul> vehicule = new ArrayList<>();

    public ParcareWorkflowServiceImpl(Parcare parcare, SesiuneFactory sesiuneFactory,
                                      CalculService calcul, ValidareService validare,
                                      AbonamentWorkflowService abonamente,
                                      NotificareService notificari, AuditService audit) {
        this.parcare = parcare;
        this.sesiuneFactory = sesiuneFactory;
        this.calcul = calcul;
        this.validare = validare;
        this.abonamente = abonamente;
        this.notificari = notificari;
        this.audit = audit;
    }

    @Override
    public void inregistreazaVehicul(Vehicul vehicul) {
        vehicule.add(vehicul);
    }

    @Override
    public SesiuneParcare intrare(NumarInmatriculare nrAuto, LocalDateTime ora) {
        // regula A4
        if (validare.areSesiuneActiva(nrAuto, sesiuni)) {
            throw new IllegalStateException("Vehiculul " + nrAuto + " este deja in parcare");
        }

        LocalDate data = ora.toLocalDate();
        LocParcare loc;

        Abonament abonament = abonamente.gasesteAbonamentValabil(nrAuto, data);
        if (abonament != null) {
            // abonatul intra pe locul lui rezervat
            loc = parcare.gasesteLoc(abonament.getIdLoc());
        } else {
            // regula A1
            Vehicul vehicul = gasesteVehicul(nrAuto);
            if (!validare.existaLocLiberOcazional(parcare, vehicul, data)) {
                throw new IllegalStateException("Nu mai sunt locuri libere");
            }
            loc = primulLocLiberOcazional(vehicul, data);
        }

        SesiuneParcare sesiune = sesiuneFactory.creeazaSesiune(nrAuto, ora, loc.getId());
        loc.setStare(StareLoc.OCUPAT);
        audit.inregistreazaCreare(sesiune, ora);
        sesiuni.add(sesiune);
        return sesiune;
    }

    @Override
    public BigDecimal sumaDePlata(NumarInmatriculare nrAuto, TipPerioada tipPerioada, LocalDateTime acum) {
        SesiuneParcare sesiune = sesiuneActivaObligatorie(nrAuto);
        Tarif tarif = calcul.gasesteTarif(parcare, sesiune.getOraIntrare().toLocalDate());

        LocalDateTime inceput = inceputPerioadaNoua(sesiune);
        BigDecimal suma = calcul.sumaPentruPerioada(tarif, tipPerioada, inceput, acum);

        // a depasit perioada platita + 15 minute: se adauga taxa de intarziere (C4)
        if (sesiune.getValabilPana() != null && !validare.poateIesiFaraTaxa(sesiune, acum)) {
            suma = suma.add(calcul.taxaIntarziere(tarif, sesiune.getTipPerioada()));
        }
        return suma;
    }

    @Override
    public Plata plateste(NumarInmatriculare nrAuto, TipPerioada tipPerioada, MetodaPlata metoda,
                          CanalPlata canal, boolean plataReusita, LocalDateTime acum) {
        SesiuneParcare sesiune = sesiuneActivaObligatorie(nrAuto);
        BigDecimal suma = sumaDePlata(nrAuto, tipPerioada, acum);

        StarePlata stare = StarePlata.ESUATA;
        if (plataReusita) {
            stare = StarePlata.REUSITA;
        }
        Plata plata = new Plata(suma, metoda, canal, stare, acum);
        sesiune.adaugaPlata(plata);

        // regula A6: doar o plata reusita prelungeste perioada
        if (validare.plataPrelungestePerioada(plata)) {
            LocalDateTime inceput = inceputPerioadaNoua(sesiune);
            sesiune.setValabilPana(calcul.valabilPana(tipPerioada, inceput, acum));
            sesiune.setTipPerioada(tipPerioada);
        } else {
            notificari.trimite(TipNotificare.PLATA_ESUATA, "Plata esuata pentru vehiculul " + nrAuto, acum);
        }

        audit.inregistreazaModificare(sesiune, acum);
        return plata;
    }

    @Override
    public boolean iesire(NumarInmatriculare nrAuto, LocalDateTime ora) {
        SesiuneParcare sesiune = sesiuneActivaObligatorie(nrAuto);

        Abonament abonament = abonamente.gasesteAbonamentValabil(nrAuto, ora.toLocalDate());
        // regula A5: fara abonament, trebuie sa fie in perioada platita + 15 minute
        if (abonament == null && !validare.poateIesiFaraTaxa(sesiune, ora)) {
            return false;
        }

        sesiune.setOraIesire(ora);
        sesiune.setStare(StareSesiune.INCHISA);
        LocParcare loc = parcare.gasesteLoc(sesiune.getIdLoc());
        if (loc != null) {
            loc.setStare(StareLoc.LIBER);
        }
        audit.inregistreazaModificare(sesiune, ora);
        return true;
    }

    @Override
    public int locuriLibere(TipZona tipZona) {
        return parcare.locuriLibere(tipZona);
    }

    @Override
    public SesiuneParcare gasesteSesiuneActiva(NumarInmatriculare nrAuto) {
        for (SesiuneParcare sesiune : sesiuni) {
            if (sesiune.getStare() == StareSesiune.ACTIVA && sesiune.getNrAuto().equals(nrAuto)) {
                return sesiune;
            }
        }
        return null;
    }

    @Override
    public List<SesiuneParcare> getSesiuni() {
        return sesiuni;
    }

    private SesiuneParcare sesiuneActivaObligatorie(NumarInmatriculare nrAuto) {
        SesiuneParcare sesiune = gasesteSesiuneActiva(nrAuto);
        if (sesiune == null) {
            throw new IllegalStateException("Vehiculul " + nrAuto + " nu este in parcare");
        }
        return sesiune;
    }

    // o plata noua incepe de unde s-a terminat cea veche (sau de la intrare)
    private LocalDateTime inceputPerioadaNoua(SesiuneParcare sesiune) {
        if (sesiune.getValabilPana() == null) {
            return sesiune.getOraIntrare();
        }
        return sesiune.getValabilPana();
    }

    private Vehicul gasesteVehicul(NumarInmatriculare nrAuto) {
        for (Vehicul vehicul : vehicule) {
            if (vehicul.getNrAuto().equals(nrAuto)) {
                return vehicul;
            }
        }
        return null;
    }

    private LocParcare primulLocLiberOcazional(Vehicul vehicul, LocalDate data) {
        for (Zona zona : parcare.getZone()) {
            if (zona.getTip() == TipZona.OCAZIONALI) {
                for (LocParcare loc : zona.getLocuri()) {
                    if (validare.poateOcupaLoc(loc, vehicul, data)) {
                        return loc;
                    }
                }
            }
        }
        throw new IllegalStateException("Nu mai sunt locuri libere");
    }
}

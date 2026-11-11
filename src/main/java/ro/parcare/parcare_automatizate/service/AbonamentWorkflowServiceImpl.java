package ro.parcare.parcare_automatizate.service;

import org.springframework.stereotype.Service;
import ro.parcare.parcare_automatizate.enums.StareAbonament;
import ro.parcare.parcare_automatizate.enums.TipNotificare;
import ro.parcare.parcare_automatizate.enums.TipZona;
import ro.parcare.parcare_automatizate.factory.AbonamentFactory;
import ro.parcare.parcare_automatizate.model.Abonament;
import ro.parcare.parcare_automatizate.model.LocParcare;
import ro.parcare.parcare_automatizate.model.NumarInmatriculare;
import ro.parcare.parcare_automatizate.model.Parcare;
import ro.parcare.parcare_automatizate.model.PerioadaAbonament;
import ro.parcare.parcare_automatizate.model.Zona;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AbonamentWorkflowServiceImpl implements AbonamentWorkflowService {

    private final Parcare parcare;
    private final AbonamentFactory abonamentFactory;
    private final ValidareService validare;
    private final CalculService calcul;
    private final NotificareService notificari;
    private final AuditService audit;

    // pana in Sprint 3 tinem abonamentele intr-o lista in memorie
    private final List<Abonament> abonamente = new ArrayList<>();

    // Spring ne da singur obiectele de care avem nevoie (prin constructor)
    public AbonamentWorkflowServiceImpl(Parcare parcare, AbonamentFactory abonamentFactory,
                                        ValidareService validare, CalculService calcul,
                                        NotificareService notificari, AuditService audit) {
        this.parcare = parcare;
        this.abonamentFactory = abonamentFactory;
        this.validare = validare;
        this.calcul = calcul;
        this.notificari = notificari;
        this.audit = audit;
    }

    @Override
    public Abonament cumparaAbonament(NumarInmatriculare nrAuto, boolean reinnoireAutomata, LocalDate dataInceput) {
        if (gasesteAbonamentValabil(nrAuto, dataInceput) != null) {
            throw new IllegalStateException("Vehiculul " + nrAuto + " are deja un abonament valabil");
        }
        // regula A3
        if (!validare.poateCreaAbonament(parcare, abonamente)) {
            throw new IllegalStateException("Nu mai sunt locuri libere pentru abonati");
        }

        LocParcare loc = primulLocLiberPentruAbonament();
        BigDecimal pret = calcul.gasesteTarif(parcare, dataInceput).getPretAbonament();

        Abonament abonament = abonamentFactory.creeazaAbonament(nrAuto, loc.getId(), reinnoireAutomata,
                dataInceput, pret);
        audit.inregistreazaCreare(abonament, LocalDateTime.now());
        abonamente.add(abonament);
        return abonament;
    }

    @Override
    public boolean reinnoieste(Abonament abonament, boolean plataReusita, LocalDateTime acum) {
        if (!plataReusita) {
            // abonamentul expira la sfarsitul perioadei curente
            notificari.trimite(TipNotificare.PLATA_ESUATA,
                    "Reinnoirea abonamentului pentru " + abonament.getNrAuto() + " a esuat", acum);
            return false;
        }

        BigDecimal pret = calcul.gasesteTarif(parcare, acum.toLocalDate()).getPretAbonament();
        PerioadaAbonament perioada = abonamentFactory.creeazaPerioadaUrmatoare(abonament, pret);
        abonament.adaugaPerioada(perioada);
        abonament.setStare(StareAbonament.ACTIV);
        audit.inregistreazaModificare(abonament, acum);
        return true;
    }

    @Override
    public Abonament gasesteAbonamentValabil(NumarInmatriculare nrAuto, LocalDate data) {
        for (Abonament abonament : abonamente) {
            if (abonament.getNrAuto().equals(nrAuto) && abonament.esteValabil(data)) {
                return abonament;
            }
        }
        return null;
    }

    @Override
    public void verificaExpirari(LocalDate azi, LocalDateTime acum) {
        for (Abonament abonament : abonamente) {
            if (abonament.getStare() == StareAbonament.ACTIV && !abonament.esteValabil(azi)) {
                if (abonament.isReinnoireAutomata()) {
                    // plata automata cu cardul salvat
                    reinnoieste(abonament, true, acum);
                } else {
                    abonament.setStare(StareAbonament.EXPIRAT);
                    audit.inregistreazaModificare(abonament, acum);
                }
            }
        }
    }

    @Override
    public List<Abonament> getAbonamente() {
        return abonamente;
    }

    private LocParcare primulLocLiberPentruAbonament() {
        for (Zona zona : parcare.getZone()) {
            if (zona.getTip() == TipZona.ABONATI) {
                for (LocParcare loc : zona.getLocuri()) {
                    if (validare.locDisponibilPentruAbonament(loc.getId(), abonamente)) {
                        return loc;
                    }
                }
            }
        }
        throw new IllegalStateException("Nu mai sunt locuri libere pentru abonati");
    }
}

package ro.parcare.parcare_automatizate.service;

import org.springframework.stereotype.Service;
import ro.parcare.parcare_automatizate.enums.StareAbonament;
import ro.parcare.parcare_automatizate.enums.StareLoc;
import ro.parcare.parcare_automatizate.enums.TipNotificare;
import ro.parcare.parcare_automatizate.enums.TipZi;
import ro.parcare.parcare_automatizate.model.Abonament;
import ro.parcare.parcare_automatizate.model.LocParcare;
import ro.parcare.parcare_automatizate.model.Notificare;
import ro.parcare.parcare_automatizate.model.Parcare;
import ro.parcare.parcare_automatizate.model.SesiuneParcare;
import ro.parcare.parcare_automatizate.model.Tarif;
import ro.parcare.parcare_automatizate.model.ZiSpeciala;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdministrareWorkflowServiceImpl implements AdministrareWorkflowService {

    private final Parcare parcare;
    private final ValidareService validare;
    private final CalculService calcul;
    private final ParcareWorkflowService parcareWorkflow;
    private final AbonamentWorkflowService abonamente;
    private final NotificareService notificari;
    private final AuditService audit;

    public AdministrareWorkflowServiceImpl(Parcare parcare, ValidareService validare, CalculService calcul,
                                           ParcareWorkflowService parcareWorkflow,
                                           AbonamentWorkflowService abonamente,
                                           NotificareService notificari, AuditService audit) {
        this.parcare = parcare;
        this.validare = validare;
        this.calcul = calcul;
        this.parcareWorkflow = parcareWorkflow;
        this.abonamente = abonamente;
        this.notificari = notificari;
        this.audit = audit;
    }

    @Override
    public void raporteazaSenzor(int numarLoc, StareLoc stare, LocalDateTime acum) {
        LocParcare loc = parcare.gasesteLocDupaNumar(numarLoc);
        if (loc == null) {
            throw new IllegalArgumentException("Nu exista locul cu numarul " + numarLoc);
        }
        loc.setStare(stare);
        verificaLocuriRezervate(acum);
    }

    @Override
    public void verificaLocuriRezervate(LocalDateTime acum) {
        List<SesiuneParcare> sesiuni = parcareWorkflow.getSesiuni();

        for (Abonament abonament : abonamente.getAbonamente()) {
            if (abonament.getStare() != StareAbonament.ACTIV) {
                continue;
            }
            LocParcare loc = parcare.gasesteLoc(abonament.getIdLoc());

            // regula A7
            if (validare.locOcupatIncorect(loc, abonament, sesiuni)) {
                notificari.trimite(TipNotificare.LOC_OCUPAT_INCORECT,
                        "Locul " + loc.getNumar() + " este ocupat, dar abonatul "
                                + abonament.getNrAuto() + " nu este in parcare", acum);
            }
            // regula A8
            if (validare.abonatParcatPeAltLoc(loc, abonament, sesiuni)) {
                notificari.trimite(TipNotificare.LOC_OCUPAT_INCORECT,
                        "Abonatul " + abonament.getNrAuto() + " nu a parcat pe locul lui ("
                                + loc.getNumar() + ")", acum);
            }
        }
    }

    @Override
    public void raporteazaMurdarie(int nivel, LocalDateTime acum) {
        // decizia de curatenie apartine administratorului
        if (nivel > parcare.getPragMurdarie()) {
            notificari.trimite(TipNotificare.MURDARIE,
                    "Nivelul de murdarie (" + nivel + ") a depasit pragul de " + parcare.getPragMurdarie(), acum);
        }
    }

    @Override
    public void modificaTarif(TipZi tipZi, BigDecimal pretOra, BigDecimal pretZi,
                              BigDecimal pretSaptamana, BigDecimal pretAbonament) {
        // regula V4: toate preturile sunt mai mari decat 0
        if (!estePozitiv(pretOra) || !estePozitiv(pretZi)
                || !estePozitiv(pretSaptamana) || !estePozitiv(pretAbonament)) {
            throw new IllegalArgumentException("Toate preturile trebuie sa fie mai mari decat 0");
        }

        for (Tarif tarif : parcare.getTarife()) {
            if (tarif.getTipZi() == tipZi) {
                tarif.setPretOra(pretOra);
                tarif.setPretZi(pretZi);
                tarif.setPretSaptamana(pretSaptamana);
                tarif.setPretAbonament(pretAbonament);
            }
        }
        audit.inregistreazaModificare(parcare, LocalDateTime.now());
    }

    @Override
    public void adaugaZiSpeciala(LocalDate data, String denumire) {
        parcare.adaugaZiSpeciala(new ZiSpeciala(data, denumire));
        audit.inregistreazaModificare(parcare, LocalDateTime.now());
    }

    @Override
    public void seteazaPragMurdarie(int prag) {
        // regula V8
        if (prag < 0 || prag > 100) {
            throw new IllegalArgumentException("Pragul de murdarie trebuie sa fie intre 0 si 100");
        }
        parcare.setPragMurdarie(prag);
        audit.inregistreazaModificare(parcare, LocalDateTime.now());
    }

    @Override
    public BigDecimal raportIncasari(LocalDate zi) {
        return calcul.incasariPeZi(parcareWorkflow.getSesiuni(), zi);
    }

    @Override
    public double raportGradOcupare() {
        return parcare.gradOcupare();
    }

    @Override
    public List<Notificare> notificariNerezolvate() {
        return notificari.nerezolvate();
    }

    @Override
    public void rezolvaNotificare(Long idNotificare) {
        notificari.marcheazaRezolvata(idNotificare);
    }

    private boolean estePozitiv(BigDecimal valoare) {
        return valoare != null && valoare.compareTo(BigDecimal.ZERO) > 0;
    }
}

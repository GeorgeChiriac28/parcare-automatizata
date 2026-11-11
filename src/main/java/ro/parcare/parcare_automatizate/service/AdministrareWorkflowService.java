package ro.parcare.parcare_automatizate.service;

import ro.parcare.parcare_automatizate.enums.StareLoc;
import ro.parcare.parcare_automatizate.enums.TipZi;
import ro.parcare.parcare_automatizate.model.Notificare;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// UC6 verificare locuri, UC7 configurare, UC8 rapoarte si notificari.
public interface AdministrareWorkflowService {

    // UC6: un senzor raporteaza starea unui loc
    void raporteazaSenzor(int numarLoc, StareLoc stare, LocalDateTime acum);

    // UC6: verifica locurile rezervate (regulile A7 si A8)
    void verificaLocuriRezervate(LocalDateTime acum);

    // camerele raporteaza nivelul de murdarie (0 - 100)
    void raporteazaMurdarie(int nivel, LocalDateTime acum);

    // UC7
    void modificaTarif(TipZi tipZi, BigDecimal pretOra, BigDecimal pretZi,
                       BigDecimal pretSaptamana, BigDecimal pretAbonament);

    void adaugaZiSpeciala(LocalDate data, String denumire);

    void seteazaPragMurdarie(int prag);

    // UC8
    BigDecimal raportIncasari(LocalDate zi);

    double raportGradOcupare();

    List<Notificare> notificariNerezolvate();

    void rezolvaNotificare(Long idNotificare);
}

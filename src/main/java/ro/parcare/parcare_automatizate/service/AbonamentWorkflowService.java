package ro.parcare.parcare_automatizate.service;

import ro.parcare.parcare_automatizate.model.Abonament;
import ro.parcare.parcare_automatizate.model.NumarInmatriculare;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// Use case-ul UC5: cumpararea si reinnoirea abonamentelor.
public interface AbonamentWorkflowService {

    Abonament cumparaAbonament(NumarInmatriculare nrAuto, boolean reinnoireAutomata, LocalDate dataInceput);

    // intoarce true daca reinnoirea a reusit
    boolean reinnoieste(Abonament abonament, boolean plataReusita, LocalDateTime acum);

    // abonamentul valabil pentru un numar la o data; null daca nu exista
    Abonament gasesteAbonamentValabil(NumarInmatriculare nrAuto, LocalDate data);

    // reinnoieste automat sau trece pe EXPIRAT abonamentele care nu mai sunt valabile
    void verificaExpirari(LocalDate azi, LocalDateTime acum);

    List<Abonament> getAbonamente();
}

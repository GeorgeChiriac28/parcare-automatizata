package ro.parcare.parcare_automatizate.service;

import ro.parcare.parcare_automatizate.model.Auditabil;

import java.time.LocalDateTime;

// Regula de audit: retine cand a fost creata si cand a fost modificata ultima data o entitate.
public interface AuditService {

    void inregistreazaCreare(Auditabil entitate, LocalDateTime data);

    void inregistreazaModificare(Auditabil entitate, LocalDateTime data);
}

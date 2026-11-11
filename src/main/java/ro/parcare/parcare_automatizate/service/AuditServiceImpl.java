package ro.parcare.parcare_automatizate.service;

import org.springframework.stereotype.Service;
import ro.parcare.parcare_automatizate.model.Auditabil;

import java.time.LocalDateTime;

@Service
public class AuditServiceImpl implements AuditService {

    @Override
    public void inregistreazaCreare(Auditabil entitate, LocalDateTime data) {
        entitate.setDataCreare(data);
        entitate.setDataModificare(data);
    }

    @Override
    public void inregistreazaModificare(Auditabil entitate, LocalDateTime data) {
        entitate.setDataModificare(data);
    }
}

package ro.parcare.parcare_automatizate.service;

import ro.parcare.parcare_automatizate.enums.TipNotificare;
import ro.parcare.parcare_automatizate.model.Notificare;

import java.time.LocalDateTime;
import java.util.List;

// Trimite si pastreaza notificarile pentru administrator.
public interface NotificareService {

    Notificare trimite(TipNotificare tip, String mesaj, LocalDateTime data);

    List<Notificare> getNotificari();

    List<Notificare> nerezolvate();

    void marcheazaRezolvata(Long idNotificare);
}

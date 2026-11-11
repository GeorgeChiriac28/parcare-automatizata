package ro.parcare.parcare_automatizate.model;

import java.time.LocalDateTime;

// Entitatile care pastreaza data crearii si data ultimei modificari (regula de audit).
public interface Auditabil {

    void setDataCreare(LocalDateTime dataCreare);

    void setDataModificare(LocalDateTime dataModificare);
}

package ro.parcare.parcare_automatizate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ro.parcare.parcare_automatizate.factory.ParcareFactory;
import ro.parcare.parcare_automatizate.model.Parcare;

// Clasa de configurare Spring.
// Aici spunem ce parcare foloseste aplicatia; serviciile o primesc automat prin constructor.
@Configuration
public class AppConfig {

    @Bean
    public Parcare parcare(ParcareFactory parcareFactory) {
        // 20 locuri abonati, 25 locuri ocazionali, 5 locuri pentru dizabilitati, prag murdarie 70
        // in Sprint 3 parcarea va fi citita din baza de date
        return parcareFactory.creeazaParcare("Parcare Centru", "Str. Principala 1", 20, 25, 5, 70);
    }
}

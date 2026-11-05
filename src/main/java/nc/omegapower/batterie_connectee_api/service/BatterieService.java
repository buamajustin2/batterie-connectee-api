package nc.omegapower.batterie_connectee_api.service;

import nc.omegapower.batterie_connectee_api.model.Batterie;
import nc.omegapower.batterie_connectee_api.repository.BatterieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class BatterieService {

    private static final Logger log = LoggerFactory.getLogger(BatterieService.class);

    // Seuils de la batterie V16
    private static final float SEUIL_TENSION_MAX = 57.6f;   // V
    private static final float SEUIL_COURANT_MAX = 100.0f;  // A
    private static final float SEUIL_TEMP_MAX    = 55.0f;   // °C
    private static final int   SEUIL_SOC_MIN     = 20;      // %

    @Autowired
    private BatterieRepository batterieRepository;

    public List<Batterie> getAllBatteries() {
        log.info("Récupération de toutes les mesures");
        return batterieRepository.findAll();
    }

    public Batterie getBatterieById(Long id) {
        return batterieRepository.findById(id).orElse(null);
    }

    public List<Batterie> getAlarmes() {
        log.info("Récupération des mesures en alarme");
        return batterieRepository.findByAlarmeTrueOrderByDateMesureDesc();
    }

    public List<Batterie> getHistorique(OffsetDateTime debut, OffsetDateTime fin) {
        log.info("Récupération de l'historique entre {} et {}", debut, fin);
        return batterieRepository.findByDateMesureBetweenOrderByDateMesureDesc(debut, fin);
    }

    public Batterie createBatterie(Batterie batterie) {
        // 1. Date serveur en UTC (stockage)
        batterie.setDateMesure(OffsetDateTime.now(ZoneOffset.UTC));

        // 2. Calcul de l'alarme
        boolean alarme = false;
        StringBuilder raison = new StringBuilder();

        if (batterie.getTension() != null && batterie.getTension() > SEUIL_TENSION_MAX) {
            alarme = true;
            raison.append(String.format("Surtension (%.1fV > %.1fV) ", 
                    batterie.getTension(), SEUIL_TENSION_MAX));
        }
        if (batterie.getCourant() != null && batterie.getCourant() > SEUIL_COURANT_MAX) {
            alarme = true;
            raison.append(String.format("Surintensité (%.1fA > %.1fA) ", 
                    batterie.getCourant(), SEUIL_COURANT_MAX));
        }
        if (batterie.getTemperature() != null && batterie.getTemperature() > SEUIL_TEMP_MAX) {
            alarme = true;
            raison.append(String.format("Surchauffe (%.1f°C > %.1f°C) ", 
                    batterie.getTemperature(), SEUIL_TEMP_MAX));
        }
        if (batterie.getSoc() != null && batterie.getSoc() < SEUIL_SOC_MIN) {
            alarme = true;
            raison.append(String.format("Batterie faible (%d%% < %d%%) ", 
                    batterie.getSoc(), SEUIL_SOC_MIN));
        }

        batterie.setAlarme(alarme);

        // 3. Log
        if (alarme) {
            log.warn("ALARME V16 : {} (soc={}%, tension={}V, courant={}A, temp={}°C)",
                    raison.toString().trim(),
                    batterie.getSoc(), batterie.getTension(),
                    batterie.getCourant(), batterie.getTemperature());
        } else {
            log.info("Mesure OK : soc={}%, tension={}V, courant={}A, temp={}°C",
                    batterie.getSoc(), batterie.getTension(),
                    batterie.getCourant(), batterie.getTemperature());
        }

        // 4. Sauvegarde
        return batterieRepository.save(batterie);
    }

    public Batterie updateBatterie(Long id, Batterie batterieDetails) {
        Batterie batterie = getBatterieById(id);
        if (batterie == null) {
            return null;
        }
        batterie.setSoc(batterieDetails.getSoc());
        batterie.setTension(batterieDetails.getTension());
        batterie.setCourant(batterieDetails.getCourant());
        batterie.setTemperature(batterieDetails.getTemperature());
        batterie.setAlarme(batterieDetails.getAlarme());
        batterie.setDateMesure(OffsetDateTime.now(ZoneOffset.UTC));
        return batterieRepository.save(batterie);
    }

    public void deleteBatterie(Long id) {
        batterieRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return batterieRepository.existsById(id);
    }
}
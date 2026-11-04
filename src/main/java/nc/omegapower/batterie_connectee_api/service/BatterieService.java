package nc.omegapower.batterie_connectee_api.service;

import nc.omegapower.batterie_connectee_api.model.Batterie;
import nc.omegapower.batterie_connectee_api.repository.BatterieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class BatterieService {

    private static final Logger log = LoggerFactory.getLogger(BatterieService.class);

    // ================================================================
    // SEUILS DE LA BATTERIE V16
    // Modifier ici si les spécifications changent.
    // ================================================================
    private static final float SEUIL_TENSION_MAX = 57.6f;   // V
    private static final float SEUIL_COURANT_MAX = 100.0f;  // A
    private static final float SEUIL_TEMP_MAX    = 55.0f;   // °C
    private static final int   SEUIL_SOC_MIN     = 20;      // %

    @Autowired
    private BatterieRepository batterieRepository;

    // ----------------------------------------------------------------
    // LECTURE
    // ----------------------------------------------------------------

    /** Historique complet (toutes les mesures, les plus récentes d'abord). */
    public List<Batterie> getAllBatteries() {
        log.info("Récupération de toutes les mesures");
        return batterieRepository.findAll();
    }

    public Batterie getBatterieById(Long id) {
        return batterieRepository.findById(id).orElse(null);
    }

    /** Toutes les mesures où une alarme s'est déclenchée. */
    public List<Batterie> getAlarmes() {
        log.info("Récupération des mesures en alarme");
        return batterieRepository.findByAlarmeTrueOrderByDateMesureDesc();
    }

    /** Historique sur une période donnée. */
    public List<Batterie> getHistorique(LocalDateTime debut, LocalDateTime fin) {
        log.info("Récupération de l'historique entre {} et {}", debut, fin);
        return batterieRepository.findByDateMesureBetweenOrderByDateMesureDesc(debut, fin);
    }

    // ----------------------------------------------------------------
    // ÉCRITURE
    // ----------------------------------------------------------------

    /**
     * Enregistre une nouvelle mesure.
     *  - Force la date côté serveur (le client ne peut pas la définir)
     *  - Calcule automatiquement le flag `alarme` selon les seuils V16
     *  - Log un warning si une alarme se déclenche
     */
    public Batterie createBatterie(Batterie batterie) {
        // 1. Date serveur (UTC)
        batterie.setDateMesure(LocalDateTime.now(ZoneOffset.UTC));

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
        if (batterie.getEtatCharge() != null && batterie.getEtatCharge() < SEUIL_SOC_MIN) {
            alarme = true;
            raison.append(String.format("Batterie faible (%d%% < %d%%) ", 
                    batterie.getEtatCharge(), SEUIL_SOC_MIN));
        }

        batterie.setAlarme(alarme);

        // 3. Log
        if (alarme) {
            log.warn("🚨 ALARME V16 : {} (soc={}%, tension={}V, courant={}A, temp={}°C)",
                    raison.toString().trim(),
                    batterie.getEtatCharge(), batterie.getTension(),
                    batterie.getCourant(), batterie.getTemperature());
        } else {
            log.info("Mesure OK : soc={}%, tension={}V, courant={}A, temp={}°C",
                    batterie.getEtatCharge(), batterie.getTension(),
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
        batterie.setEtatCharge(batterieDetails.getEtatCharge());
        batterie.setTension(batterieDetails.getTension());
        batterie.setCourant(batterieDetails.getCourant());
        batterie.setTemperature(batterieDetails.getTemperature());
        batterie.setAlarme(batterieDetails.getAlarme());
        batterie.setDateMesure(LocalDateTime.now(ZoneOffset.UTC));
        return batterieRepository.save(batterie);
    }

    public void deleteBatterie(Long id) {
        batterieRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return batterieRepository.existsById(id);
    }
}
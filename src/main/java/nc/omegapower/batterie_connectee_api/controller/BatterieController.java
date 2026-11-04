package nc.omegapower.batterie_connectee_api.controller;

import jakarta.validation.Valid;
import nc.omegapower.batterie_connectee_api.model.Batterie;
import nc.omegapower.batterie_connectee_api.service.BatterieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/batteries")
public class BatterieController {

    private static final Logger log = LoggerFactory.getLogger(BatterieController.class);

    @Autowired
    private BatterieService batterieService;

    // ----------------------------------------------------------------
    // HISTORIQUE
    // ----------------------------------------------------------------

    /** Toutes les mesures (les plus récentes d'abord). */
    @GetMapping
    public List<Batterie> getAllBatteries() {
        log.info("GET /api/batteries");
        return batterieService.getAllBatteries();
    }

    /** Une mesure par son id. */
    @GetMapping("/{id}")
    public ResponseEntity<Batterie> getBatterieById(@PathVariable Long id) {
        Batterie batterie = batterieService.getBatterieById(id);
        if (batterie != null) {
            return ResponseEntity.ok(batterie);
        }
        return ResponseEntity.notFound().build();
    }

    /** Toutes les mesures où une alarme s'est déclenchée. */
    @GetMapping("/alarmes")
    public List<Batterie> getAlarmes() {
        log.info("GET /api/batteries/alarmes");
        return batterieService.getAlarmes();
    }

    /**
     * Historique sur une période.
     * Exemple : /api/batteries/historique?debut=2026-11-04T00:00:00&fin=2026-11-04T23:59:59
     */
    @GetMapping("/historique")
    public List<Batterie> getHistorique(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        log.info("GET /api/batteries/historique?debut={}&fin={}", debut, fin);
        return batterieService.getHistorique(debut, fin);
    }

    // ----------------------------------------------------------------
    // ÉCRITURE
    // ----------------------------------------------------------------

    /** Enregistre une nouvelle mesure (calcul alarme automatique). */
    @PostMapping
    public ResponseEntity<Batterie> createBatterie(@Valid @RequestBody Batterie batterie) {
        log.info("POST /api/batteries {}", batterie);
        Batterie newBatterie = batterieService.createBatterie(batterie);
        return ResponseEntity.status(HttpStatus.CREATED).body(newBatterie);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Batterie> updateBatterie(@PathVariable Long id,
                                                   @Valid @RequestBody Batterie batterie) {
        Batterie updated = batterieService.updateBatterie(id, batterie);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBatterie(@PathVariable Long id) {
        if (batterieService.existsById(id)) {
            batterieService.deleteBatterie(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
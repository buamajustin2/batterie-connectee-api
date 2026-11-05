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

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@RestController
@RequestMapping("/api/batteries")
public class BatterieController {

    private static final Logger log = LoggerFactory.getLogger(BatterieController.class);

    @Autowired
    private BatterieService batterieService;

    // ----------------------------------------------------------------
    // LECTURE
    // ----------------------------------------------------------------

    @GetMapping
    public List<Batterie> getAllBatteries() {
        log.info("GET /api/batteries");
        return batterieService.getAllBatteries();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Batterie> getBatterieById(@PathVariable Long id) {
        Batterie batterie = batterieService.getBatterieById(id);
        if (batterie != null) {
            return ResponseEntity.ok(batterie);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/alarmes")
    public List<Batterie> getAlarmes() {
        log.info("GET /api/batteries/alarmes");
        return batterieService.getAlarmes();
    }

    /**
     * Historique sur une période (Date simple, ex: 2026-11-04)
     */
    @GetMapping("/historique")
    public List<Batterie> getHistorique(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        
        log.info("GET /api/batteries/historique?debut={}&fin={}", debut, fin);
        
        // Conversion en OffsetDateTime (UTC) pour correspondre à la base
        OffsetDateTime debutTime = debut.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime finTime = fin.atTime(23, 59, 59).atOffset(ZoneOffset.UTC);
        
        return batterieService.getHistorique(debutTime, finTime);
    }

    // ----------------------------------------------------------------
    // ÉCRITURE
    // ----------------------------------------------------------------

    @PostMapping
    public ResponseEntity<Batterie> createBatterie(@Valid @RequestBody Batterie batterie) {
        log.info("POST /api/batteries {}", batterie);
        Batterie newBatterie = batterieService.createBatterie(batterie);
        return ResponseEntity.status(HttpStatus.CREATED).body(newBatterie);
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
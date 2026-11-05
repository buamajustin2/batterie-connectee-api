package nc.omegapower.batterie_connectee_api.controller;

import nc.omegapower.batterie_connectee_api.model.Batterie;
import nc.omegapower.batterie_connectee_api.service.BatterieService;
import nc.omegapower.batterie_connectee_api.utils.Decodeur;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/trames")
public class TrameController {

    private final BatterieService batterieService;

    public TrameController(BatterieService batterieService) {
        this.batterieService = batterieService;
    }

    /**
     * Reçoit la trame binaire brute (5 octets) envoyée par l'ER2.
     */
    @PostMapping(consumes = "application/octet-stream")
    public ResponseEntity<Batterie> recevoirTrameBinaire(@RequestBody byte[] octets) {
        
        // 1. Convertir le tableau de byte[] (binaire) en List<Integer> pour le décodeur
        List<Integer> listeOctets = new ArrayList<>();
        for (byte b : octets) {
            listeOctets.add(b & 0xFF); // Convertit en entier non signé (0-255)
        }

        // 2. Décoder la trame
        Decodeur.BatterieDecodee decodee = Decodeur.decoder(listeOctets);

        // 3. Construire l'objet Batterie
        Batterie batterie = new Batterie();
        batterie.setSoc(decodee.soc);
        batterie.setTension(decodee.tension);
        batterie.setCourant(decodee.courant);
        batterie.setTemperature(decodee.temperature);
        
        // 4. Sauvegarder (le service calcule les alarmes)
        Batterie saved = batterieService.createBatterie(batterie);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
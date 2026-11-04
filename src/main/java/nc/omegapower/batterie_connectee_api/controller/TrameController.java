package nc.omegapower.batterie_connectee_api.controller;

import jakarta.validation.Valid;
import nc.omegapower.batterie_connectee_api.dto.TrameDto;
import nc.omegapower.batterie_connectee_api.model.Batterie;
import nc.omegapower.batterie_connectee_api.service.BatterieService;
import nc.omegapower.batterie_connectee_api.utils.Decodeur;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trames")
public class TrameController {

    private final BatterieService batterieService;

    public TrameController(BatterieService batterieService) {
        this.batterieService = batterieService;
    }

    /**
     * Reçoit une trame binaire de la carte en 4G (ER2).
     * Ex de body JSON : { "octets": [132, 164, 144, 0, 87, 0, 0, 0, 0, 1] }
     */
    @PostMapping
    public ResponseEntity<Batterie> recevoirTrame(@Valid @RequestBody TrameDto trameDto) {

        // 1. Décoder la trame binaire
        Decodeur.MesureDecodee decodee = Decodeur.decoder(trameDto.octets());

        // 2. Construire l'objet Batterie pour la base de données
        Batterie batterie = new Batterie();
        batterie.setEtatCharge(decodee.soc);
        batterie.setTension(decodee.tension);
        batterie.setCourant(decodee.courant);
        batterie.setTemperature(decodee.temperature);
        
        // 3. Sauvegarder via le service (qui gère aussi le calcul des alarmes)
        Batterie saved = batterieService.createBatterie(batterie);

        // 4. Renvoyer la réponse JSON
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
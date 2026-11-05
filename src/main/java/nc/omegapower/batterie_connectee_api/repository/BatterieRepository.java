package nc.omegapower.batterie_connectee_api.repository;

import nc.omegapower.batterie_connectee_api.model.Batterie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate; // <-- L'IMPORT MANQUANT
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BatterieRepository extends JpaRepository<Batterie, Long> {

    List<Batterie> findByAlarmeTrueOrderByDateMesureDesc();

    // Attention : ici vous avez mis LocalDate, mais dans le Service vous utilisez LocalDateTime !
    // Il faut choisir. Mettons LocalDateTime pour être cohérent avec la base de données.
    List<Batterie> findByDateMesureBetweenOrderByDateMesureDesc(
            LocalDateTime debut, LocalDateTime fin);
}
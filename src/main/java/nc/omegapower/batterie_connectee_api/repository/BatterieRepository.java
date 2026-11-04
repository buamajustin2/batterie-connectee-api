package nc.omegapower.batterie_connectee_api.repository;

import nc.omegapower.batterie_connectee_api.model.Batterie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BatterieRepository extends JpaRepository<Batterie, Long> {

    /** Toutes les mesures où une alarme s'est déclenchée, les plus récentes d'abord. */
    List<Batterie> findByAlarmeTrueOrderByDateMesureDesc();

    /** Toutes les mesures entre deux dates, les plus récentes d'abord. */
    List<Batterie> findByDateMesureBetweenOrderByDateMesureDesc(
            LocalDateTime debut, LocalDateTime fin);
}
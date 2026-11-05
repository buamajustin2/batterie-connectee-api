package nc.omegapower.batterie_connectee_api.repository;

import nc.omegapower.batterie_connectee_api.model.Batterie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface BatterieRepository extends JpaRepository<Batterie, Long> {

    List<Batterie> findByAlarmeTrueOrderByDateMesureDesc();

    List<Batterie> findByDateMesureBetweenOrderByDateMesureDesc(
            OffsetDateTime debut, OffsetDateTime fin);
}
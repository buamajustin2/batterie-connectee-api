package nc.omegapower.batterie_connectee_api.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record TrameDto(
        @NotNull List<Integer> octets
) {}
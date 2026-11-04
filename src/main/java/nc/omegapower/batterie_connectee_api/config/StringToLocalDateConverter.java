package nc.omegapower.batterie_connectee_api.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Convertit une String en LocalDate.
 * Accepte :
 *   - "2026-09-30"               → 2026-09-30
 *   - "2026-09-30T12:08:41"      → 2026-09-30  (extrait la date)
 *   - "2026-09-30T12:08:41.123Z" → 2026-09-30
 */
@Component
public class StringToLocalDateConverter implements Converter<String, LocalDate> {

    @Override
    public LocalDate convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }

        String trimmed = source.trim();

        // Cas 1 : date seule (yyyy-MM-dd)
        if (trimmed.length() == 10) {
            return LocalDate.parse(trimmed, DateTimeFormatter.ISO_DATE);
        }

        // Cas 2 : datetime ISO → extrait la date
        try {
            return LocalDateTime.parse(trimmed, DateTimeFormatter.ISO_DATE_TIME).toLocalDate();
        } catch (Exception ignored) {
            // ignore
        }

        // Cas 3 : datetime avec timezone
        try {
            return java.time.OffsetDateTime.parse(trimmed).toLocalDate();
        } catch (Exception ignored) {
            // ignore
        }

        // Dernier recours : tenter un parse direct
        return LocalDate.parse(trimmed);
    }
}
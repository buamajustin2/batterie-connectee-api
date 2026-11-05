package nc.omegapower.batterie_connectee_api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

@Entity
@Table(name = "batterie")
public class Batterie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Min(0)
    @Max(100)
    @Column(name = "soc")
    private Integer soc;

    @NotNull
    private Float tension;

    @NotNull
    private Float courant;

    @NotNull
    private Float temperature;

    @NotNull
    private Boolean alarme;

    // Formatage pour l'affichage JSON (Heure de Nouméa)
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Pacific/Noumea")
    @Column(name = "date_mesure")
    private OffsetDateTime dateMesure;

    public Batterie() {}

    public Batterie(Integer soc, Float tension, Float courant,
                    Float temperature, Boolean alarme, OffsetDateTime dateMesure) {
        this.soc = soc;
        this.tension = tension;
        this.courant = courant;
        this.temperature = temperature;
        this.alarme = alarme;
        this.dateMesure = dateMesure;
    }

    public Long getId() { return id; }
    public Integer getSoc() { return soc; }
    public Float getTension() { return tension; }
    public Float getCourant() { return courant; }
    public Float getTemperature() { return temperature; }
    public Boolean getAlarme() { return alarme; }
    public OffsetDateTime getDateMesure() { return dateMesure; }

    public void setId(Long id) { this.id = id; }
    public void setSoc(Integer soc) { this.soc = soc; }
    public void setTension(Float tension) { this.tension = tension; }
    public void setCourant(Float courant) { this.courant = courant; }
    public void setTemperature(Float temperature) { this.temperature = temperature; }
    public void setAlarme(Boolean alarme) { this.alarme = alarme; }
    public void setDateMesure(OffsetDateTime dateMesure) { this.dateMesure = dateMesure; }
}
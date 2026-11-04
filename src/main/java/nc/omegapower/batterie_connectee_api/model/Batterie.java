package nc.omegapower.batterie_connectee_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "batterie")
public class Batterie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Min(0)
    @Max(100)
    @Column(name = "etat_charge")
    private Integer etatCharge;

    @NotNull
    private Float tension;

    @NotNull
    private Float courant;

    @NotNull
    private Float temperature;

    @NotNull
    private Boolean alarme;

    @Column(name = "date_mesure")
    private LocalDateTime dateMesure;

    public Batterie() {}

    public Batterie(Integer etatCharge, Float tension, Float courant,
                    Float temperature, Boolean alarme, LocalDateTime dateMesure) {
        this.etatCharge = etatCharge;
        this.tension = tension;
        this.courant = courant;
        this.temperature = temperature;
        this.alarme = alarme;
        this.dateMesure = dateMesure;
    }

    // GETTERS
    public Long getId() { return id; }
    public Integer getEtatCharge() { return etatCharge; }
    public Float getTension() { return tension; }
    public Float getCourant() { return courant; }
    public Float getTemperature() { return temperature; }
    public Boolean getAlarme() { return alarme; }
    public LocalDateTime getDateMesure() { return dateMesure; }

    // SETTERS
    public void setId(Long id) { this.id = id; }
    public void setEtatCharge(Integer etatCharge) { this.etatCharge = etatCharge; }
    public void setTension(Float tension) { this.tension = tension; }
    public void setCourant(Float courant) { this.courant = courant; }
    public void setTemperature(Float temperature) { this.temperature = temperature; }
    public void setAlarme(Boolean alarme) { this.alarme = alarme; }
    public void setDateMesure(LocalDateTime dateMesure) { this.dateMesure = dateMesure; }
}
// Dans createBatterie :
if (batterie.getSoc() != null && batterie.getSoc() < SEUIL_SOC_MIN) {
    alarme = true;
    raison.append(String.format("Batterie faible (%d%% < %d%%) ",
            batterie.getSoc(), SEUIL_SOC_MIN));
}

// Dans les logs :
log.warn("🚨 ALARME V16 : {} (soc={}%, tension={}V, courant={}A, temp={}°C)",
        raison.toString().trim(),
        batterie.getSoc(), batterie.getTension(),
        batterie.getCourant(), batterie.getTemperature());
// ...
log.info("Mesure OK : soc={}%, tension={}V, courant={}A, temp={}°C",
        batterie.getSoc(), batterie.getTension(),
        batterie.getCourant(), batterie.getTemperature());

// Dans updateBatterie :
batterie.setSoc(batterieDetails.getSoc());
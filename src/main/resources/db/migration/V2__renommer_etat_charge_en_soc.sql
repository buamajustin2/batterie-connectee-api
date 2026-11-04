-- V2__renommer_etat_charge_en_soc.sql
-- Renomme la colonne etat_charge en soc pour correspondre au nouveau code Java
ALTER TABLE batterie CHANGE COLUMN etat_charge soc INT;
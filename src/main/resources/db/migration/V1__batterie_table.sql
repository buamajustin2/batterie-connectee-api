-- V1__batterie_table.sql
CREATE TABLE batterie (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- ← INT devient BIGINT
  etat_charge INT,
  tension FLOAT,
  courant FLOAT,
  temperature FLOAT,
  alarme BOOLEAN,
  date_mesure DATETIME DEFAULT CURRENT_TIMESTAMP
);
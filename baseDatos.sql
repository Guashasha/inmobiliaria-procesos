DROP DATABASE IF EXISTS inmobiliaria;
CREATE DATABASE IF NOT EXISTS inmobiliaria;

USE inmobiliaria;

DROP USER IF EXISTS "clientePXNG"@"%";
CREATE USER IF NOT EXISTS "clientePXNG" IDENTIFIED BY "papuCOIL";
GRANT INSERT, SELECT, UPDATE on inmobiliaria.* to "clientePXNG"@"%";

DROP USER IF EXISTS "adminPXNG"@"%";
CREATE USER IF NOT EXISTS "adminPXNG"@"%" IDENTIFIED BY "RobaloBurbuja";
GRANT all on inmobiliaria.* to "adminPXNG"@"%";

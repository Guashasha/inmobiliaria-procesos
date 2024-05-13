DROP DATABASE IF EXISTS PXNGAgency;
CREATE DATABASE IF NOT EXISTS PXNGAgency;

USE PXNGAgency;

CREATE TABLE `property` (
  `id` int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `title` varchar(150) NOT NULL,
  `shortDescription` varchar(200) NOT NULL,
  `fullDescription` varchar(500) NOT NULL,
  `type` ENUM ('all', 'building', 'house', 'apartment', 'premises') NOT NULL,
  `price` float NOT NULL,
  `state` ENUM ('available', 'occupied', 'suspended') NOT NULL,
  `direction` varchar(400) NOT NULL,
  `houseOwner` int NOT NULL,
  `action` ENUM ('sell', 'rent') NOT NULL
);

CREATE TABLE `propertyPictures` (
  `id` int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `picture` blob NOT NULL,
  `propertyId` int NOT NULL
);

CREATE TABLE `houseOwner` (
  `id` int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `email` varchar(70) NOT NULL,
  `phone` char(10) NOT NULL
);

CREATE TABLE `account` (
  `id` int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `type` ENUM ('client', 'agent') NOT NULL,
  `email` varchar(70) NOT NULL,
  `phone` char(10) NOT NULL,
  `password` varchar(300) NOT NULL,
  `profileImage` blob
);

CREATE TABLE `agent` (
  `accountId` int PRIMARY KEY NOT NULL,
  `personelNumber` varchar(20) NOT NULL
);

CREATE TABLE `search` (
  `clientId` int PRIMARY KEY NOT NULL,
  `searchTerm` varchar(100),
  `propertyType` ENUM ('all', 'building', 'house', 'apartment', 'premises') NOT NULL
);

CREATE TABLE `query` (
  `id` int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `clientId` int NOT NULL,
  `idProperty` int NOT NULL
);

CREATE TABLE `visit` (
  `id` int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `clientId` int NOT NULL,
  `propertyId` int NOT NULL,
  `date` Date NOT NULL,
  `time` time NOT NULL
);

ALTER TABLE `houseOwner` ADD FOREIGN KEY (`id`) REFERENCES `property` (`houseOwner`);

ALTER TABLE `propertyPictures` ADD FOREIGN KEY (`propertyId`) REFERENCES `property` (`id`);

ALTER TABLE `agent` ADD FOREIGN KEY (`accountId`) REFERENCES `account` (`id`);

ALTER TABLE `search` ADD FOREIGN KEY (`clientId`) REFERENCES `account` (`id`);

ALTER TABLE `query` ADD FOREIGN KEY (`clientId`) REFERENCES `account` (`id`);

ALTER TABLE `query` ADD FOREIGN KEY (`idProperty`) REFERENCES `property` (`id`);

ALTER TABLE `visit` ADD FOREIGN KEY (`clientId`) REFERENCES `account` (`id`);

ALTER TABLE `visit` ADD FOREIGN KEY (`propertyId`) REFERENCES `property` (`id`);

DROP USER IF EXISTS "clientePXNG"@"%";
CREATE USER IF NOT EXISTS "clientePXNG" IDENTIFIED BY "papuCOIL";
GRANT INSERT, SELECT, UPDATE on PXNGAgency.* to "clientePXNG"@"%";

DROP USER IF EXISTS "adminPXNG"@"%";
CREATE USER IF NOT EXISTS "adminPXNG"@"%" IDENTIFIED BY "RobaloBurbuja";
GRANT all on PXNGAgency.* to "adminPXNG"@"%";

CREATE DATABASE IF NOT EXISTS `esoft_projeto`;
USE `esoft_projeto`;

CREATE TABLE IF NOT EXISTS `eventos` (
  `Id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Nome` varchar(50) NOT NULL,
  `Inicio` date NOT NULL,
  `Fim` date NOT NULL,
  `Pais` varchar(75) NOT NULL,
  `Local` varchar(75) NOT NULL,
  `Deleted_At` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`Id`)
);

CREATE TABLE IF NOT EXISTS `modalidades` (
  `Id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Nome` varchar(50) NOT NULL,
  `Tipo_De_Contagem` enum('S','M') NOT NULL,
  PRIMARY KEY (`Id`)
);

CREATE TABLE IF NOT EXISTS `provas` (
  `Id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Id_Evento` int(10) unsigned NOT NULL,
  `Id_Modalidade` int(10) unsigned NOT NULL,
  `Sexo` enum('M','F','X') NOT NULL,
  `Minimos` smallint(5) unsigned NOT NULL,
  `Atletas_Por_Provas` tinyint(1) unsigned NOT NULL DEFAULT 8,
  `Delete_At` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `Eventos_Provas` (`Id_Evento`),
  KEY `Modalidades_Provas` (`Id_Modalidade`),
  CONSTRAINT `Eventos_Provas` FOREIGN KEY (`Id_Evento`) REFERENCES `eventos` (`Id`) ON UPDATE CASCADE,
  CONSTRAINT `Modalidades_Provas` FOREIGN KEY (`Id_Modalidade`) REFERENCES `modalidades` (`Id`) ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS `etapas` (
  `Id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Id_Prova` int(10) unsigned DEFAULT NULL,
  `Nome` varchar(50) NOT NULL,
  `Data` date NOT NULL,
  `Hora` time NOT NULL,
  PRIMARY KEY (`Id`),
  KEY `Provas_Etapas` (`Id_Prova`),
  CONSTRAINT `Provas_Etapas` FOREIGN KEY (`Id_Prova`) REFERENCES `provas` (`Id`) ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS `atletas` (
  `Id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Nome` varchar(255) NOT NULL,
  `Pais` varchar(75) NOT NULL,
  `Genero` enum('M','F') NOT NULL,
  `Data_De_Nascimento` date NOT NULL,
  `Contacto` varchar(255) NOT NULL,
  PRIMARY KEY (`Id`)
);

CREATE TABLE IF NOT EXISTS `inscricoes` (
  `Id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Id_Prova` int(10) unsigned NOT NULL,
  `Id_Atleta` int(10) unsigned NOT NULL,
  PRIMARY KEY (`Id`),
  KEY `Inscricoes_Provas` (`Id_Prova`),
  KEY `Inscricoes_Atletas` (`Id_Atleta`),
  CONSTRAINT `Inscricoes_Atletas` FOREIGN KEY (`Id_Atleta`) REFERENCES `atletas` (`Id`) ON UPDATE CASCADE,
  CONSTRAINT `Inscricoes_Provas` FOREIGN KEY (`Id_Prova`) REFERENCES `provas` (`Id`) ON UPDATE CASCADE
);


CREATE TABLE IF NOT EXISTS `etapa_grupos` (
  `Id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Id_Etapa` int(10) unsigned NOT NULL,
  `Id_Atleta` int(10) unsigned NOT NULL,
  `Resultado` float DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `Etapas_Grupos` (`Id_Etapa`),
  KEY `Etapas_Atletas` (`Id_Atleta`),
  CONSTRAINT `Etapas_Atletas` FOREIGN KEY (`Id_Atleta`) REFERENCES `atletas` (`Id`) ON UPDATE CASCADE,
  CONSTRAINT `Etapas_Grupos` FOREIGN KEY (`Id_Etapa`) REFERENCES `etapas` (`Id`) ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS `recordes` (
  `Id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Id_Modalidade` int(10) unsigned NOT NULL,
  `Id_Atleta` int(10) unsigned NOT NULL,
  `Resultado` float NOT NULL,
  PRIMARY KEY (`Id`),
  KEY `Modalidades_Recordes` (`Id_Modalidade`),
  KEY `Atletas_Recordes` (`Id_Atleta`),
  CONSTRAINT `Atletas_Recordes` FOREIGN KEY (`Id_Atleta`) REFERENCES `atletas` (`Id`) ON UPDATE CASCADE,
  CONSTRAINT `Modalidades_Recordes` FOREIGN KEY (`Id_Modalidade`) REFERENCES `modalidades` (`Id`) ON UPDATE CASCADE
);
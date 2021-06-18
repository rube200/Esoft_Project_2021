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
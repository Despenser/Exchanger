CREATE TABLE `CLIENT` (
  `ID`            BIGINT(20)  NOT NULL AUTO_INCREMENT,
  `BIRTHDAY`      DATE        NOT NULL,
  `GENDER`        VARCHAR(10) NOT NULL,
  `MIDDLE_NAME`   VARCHAR(45) DEFAULT NULL,
  `NAME`          VARCHAR(45) NOT NULL,
  `SURNAME`       VARCHAR(45) NOT NULL,
  `CONTACT_ID`    BIGINT(20)  DEFAULT NULL,
  `PASSPORT_ID`   BIGINT(20)  DEFAULT NULL,

  PRIMARY KEY          (`id`),
  KEY `FK_CONTACT_ID`  (`CONTACT_ID`),
  KEY `FK_PASSPORT_ID` (`PASSPORT_ID`),
  CONSTRAINT `FK_CONTACT_ID`  FOREIGN KEY (`CONTACT_ID`)  REFERENCES `contact`  (`ID`),
  CONSTRAINT `FK_PASSPORT_ID` FOREIGN KEY (`PASSPORT_ID`) REFERENCES `passport` (`ID`)
)
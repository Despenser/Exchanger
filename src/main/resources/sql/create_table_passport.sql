CREATE TABLE `PASSPORT` (
  `ID`           bigint(20)    NOT NULL AUTO_INCREMENT,
  `BIRTH_PLACE`  varchar(255)  NOT NULL,
  `DATE_ISSUE`   date          NOT NULL,
  `ISSUED_BY`    varchar(255)  NOT NULL,
  `NUMBER`       int(11)       NOT NULL,
  `REGISTRATION` varchar(255)  NOT NULL,
  `SERIAL`       int(11)       NOT NULL,
  `UNIT_CODE`    int(11)       NOT NULL,

  PRIMARY KEY (`id`)
)
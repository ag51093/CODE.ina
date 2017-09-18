


-- Creazione del database

DROP DATABASE IF EXISTS watcherdb;
CREATE DATABASE IF NOT EXISTS watcherdb;
USE watcherdb;



-- Creazione table utente

DROP TABLE IF EXISTS utente;
CREATE TABLE IF NOT EXISTS utente (
  email varchar(64) NOT NULL,
  password char(64) NOT NULL,
  flagadmin tinyint(1) NOT NULL,
  PRIMARY KEY (email)
);

-- Popolamento table utente

INSERT INTO utente (email, password, flagadmin) VALUES
('ag51093@gmail.com', '1ba3d16e9881959f8c9a9762854f72c6e6321cdd44358a10a4e939033117eab9', 1),
('ivanlamparelli@hotmail.it', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 0),
('lamparelli.ivan@gmail.com', '1ba3d16e9881959f8c9a9762854f72c6e6321cdd44358a10a4e939033117eab9', 0),
('scay93@hotmail.it', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 0),
('matteo.forina.93@gmail.com', '1ba3d16e9881959f8c9a9762854f72c6e6321cdd44358a10a4e939033117eab9', 1),
('mattuzz93@gmail.com', '1ba3d16e9881959f8c9a9762854f72c6e6321cdd44358a10a4e939033117eab9', 0),
('scay93g@gmail.com', '1ba3d16e9881959f8c9a9762854f72c6e6321cdd44358a10a4e939033117eab9', 0);



-- Creazione table ambiente

DROP TABLE IF EXISTS ambiente;
CREATE TABLE IF NOT EXISTS ambiente (
  proprietario varchar(64) NOT NULL,
  tipo varchar(32) NOT NULL,
  nome varchar(32) NOT NULL,
  PRIMARY KEY (proprietario, nome),
  FOREIGN KEY (proprietario) REFERENCES utente(email) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Popolamento table ambiente

INSERT INTO ambiente (proprietario, tipo, nome) VALUES 
('ivanlamparelli@hotmail.it', 'serra', 'serra san sabino'),
('scay93@hotmail.it', 'campo eolico', 'minervino murge'),
('lamparelli.ivan@gmail.com', 'campo eolico', 'monticchio'),
('lamparelli.ivan@gmail.com', 'campo fotovoltaico', 'taranto fotovoltaico'),
('lamparelli.ivan@gmail.com', 'silos', 'granaio divella bari 1'),
('lamparelli.ivan@gmail.com', 'silos', 'granaio divella bari 2'),
('scay93@hotmail.it', 'campo fotovoltaico', 'alberobello sunsystems'),
('mattuzz93@gmail.com', 'silos', 'stabilimento peroni bari'),
('mattuzz93@gmail.com', 'campo agricolo', 'canosa uliveto'),
('scay93g@gmail.com', 'campo agricolo', 'bari vigneto'),
('scay93g@gmail.com', 'serra', 'brindisi serra');



-- Creazione table dato installazione

DROP TABLE IF EXISTS datoinstallazione;
CREATE TABLE IF NOT EXISTS datoinstallazione (
  proprietarioambiente varchar(64) NOT NULL,
  nomeambiente varchar(32) NOT NULL,
  codicesensore varchar(32) NOT NULL,
  posizione varchar(4) NOT NULL,
  PRIMARY KEY (proprietarioambiente, nomeambiente, codicesensore),
  UNIQUE (codicesensore),
  FOREIGN KEY (proprietarioambiente, nomeambiente) REFERENCES ambiente(proprietario,nome) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Popolamento table dato installazione

INSERT INTO datoinstallazione (proprietarioambiente, nomeambiente, codicesensore, posizione) VALUES
('ivanlamparelli@hotmail.it', 'serra san sabino', 'igr-hit-er49#1974', 'f7'),
('ivanlamparelli@hotmail.it', 'serra san sabino', 'ter-pan-cb46#5685', 'c5'),
('scay93@hotmail.it', 'minervino murge', 'ane-sch-kr11#3677', 'c1'),
('lamparelli.ivan@gmail.com', 'granaio divella bari 1', 'ter-tex-pd10#2689', 'g8'),
('lamparelli.ivan@gmail.com', 'granaio divella bari 1', 'ant-pan-re41#7953', 'c4'),
('lamparelli.ivan@gmail.com', 'granaio divella bari 2', 'ant-pan-re41#7954', 'a3'),
('lamparelli.ivan@gmail.com', 'granaio divella bari 2', 'ter-hit-fu23#2849', 'f6'),
('lamparelli.ivan@gmail.com', 'monticchio', 'ane-sie-sa77#9314', 'b2'),
('lamparelli.ivan@gmail.com', 'taranto fotovoltaico', 'fot-sch-wq51#4999', 'a3'),
('scay93@hotmail.it', 'alberobello sunsystems', 'fot-tex-kj32#6179', 'a2'),
('mattuzz93@gmail.com', 'stabilimento peroni bari', 'ant-pan-re41#7952', 'c5'),
('mattuzz93@gmail.com', 'stabilimento peroni bari', 'ter-sie-ys71#5713', 'a3'),
('mattuzz93@gmail.com', 'canosa uliveto', 'igr-hit-er46#1973', 'a1'),
('mattuzz93@gmail.com', 'canosa uliveto', 'fot-tex-kj32#6178', 'k6'),
('mattuzz93@gmail.com', 'canosa uliveto', 'plu-pan-yd25#4681', 'u4'),
('mattuzz93@gmail.com', 'canosa uliveto', 'ter-hit-fu23#2848', 'e3'),
('scay93g@gmail.com', 'bari vigneto', 'fot-sch-se84#7835', 'b5'),
('scay93g@gmail.com', 'bari vigneto', 'igr-pan-ht69#1958', 'h2'),
('scay93g@gmail.com', 'brindisi serra', 'igr-tex-qw83#1111', 'd8'),
('scay93g@gmail.com', 'bari vigneto', 'plu-sie-po54#7612', 'd1'),
('scay93g@gmail.com', 'bari vigneto', 'ter-hit-fu23#2847', 'f9'),
('scay93g@gmail.com', 'brindisi serra', 'ter-sie-ys71#5712', 'd7');



-- Creazione table tipi sensore

DROP TABLE IF EXISTS tiposensore;
CREATE TABLE IF NOT EXISTS tiposensore (
  tiposensore varchar(16) NOT NULL,
  unitmisura varchar (8) NOT NULL,
  strutturarilevazione varchar (24) NOT NULL,
  PRIMARY KEY (tiposensore)
);

-- Popolamento table tipi sensore

INSERT INTO tiposensore(tiposensore, unitmisura, strutturarilevazione) VALUES
('anemometro', 'km/h', 'GGMMAAAAoommFVVVVVV'),
('antincendio', 'on/off', 'FVVAAAAMMGGoomm'),
('fotometro', 'lx', 'FVVVVVVAAAAMMGGoomm'),
('igrometro', '%', 'GGMMAAAAoommFVV'),
('pluviometro', 'mm', 'AAAAMMGGoommFVVVVVV'),
('termometro', 'C°', 'FVVAAAAMMGGoomm');



-- Creazione table sensore

DROP TABLE IF EXISTS sensore;
CREATE TABLE IF NOT EXISTS sensore (
  codice varchar(32) NOT NULL,
  tipo varchar(16) NOT NULL,
  marca varchar(32) NOT NULL,
  modello varchar(16) NOT NULL,
  PRIMARY KEY (codice),
  FOREIGN KEY (codice) REFERENCES datoinstallazione(codicesensore) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (tipo) REFERENCES tiposensore(tiposensore) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Popolamento table sensore

INSERT INTO sensore (codice, tipo, marca, modello) VALUES 
('ane-sch-kr11#3677', 'anemometro', 'schneider electric', 'kr11'),
('ane-sie-sa77#9314', 'anemometro', 'siemens', 'sa77'),
('ant-pan-re41#7952', 'antincendio', 'panasonic', 're41'),
('ant-pan-re41#7953', 'antincendio', 'panasonic', 're41'),
('ant-pan-re41#7954', 'antincendio', 'panasonic', 're41'),
('fot-sch-se84#7835', 'fotometro', 'schneider electric', 'se84'),
('fot-sch-wq51#4999', 'fotometro', 'schneider electric', 'wq51'),
('fot-tex-kj32#6178', 'fotometro', 'texas instrument', 'kj32'),
('fot-tex-kj32#6179', 'fotometro', 'texas instrument', 'kj32'),
('igr-hit-er46#1973', 'igrometro', 'hitachi', 'er49'),
('igr-hit-er49#1974', 'igrometro', 'hitachi', 'er49'),
('igr-pan-ht69#1958', 'igrometro', 'panasonic', 'ht69'),
('igr-tex-qw83#1111', 'igrometro', 'texas instrument', 'qw83'),
('plu-pan-yd25#4681', 'pluviometro', 'panasonic', 'yd25'),
('plu-sie-po54#7612', 'pluviometro', 'siemens', 'po54'),
('ter-hit-fu23#2847', 'termometro', 'hitachi', 'fu23'),
('ter-hit-fu23#2848', 'termometro', 'hitachi', 'fu23'),
('ter-hit-fu23#2849', 'termometro', 'hitachi', 'fu23'),
('ter-pan-cb46#5685', 'termometro', 'panasonic', 'cb46'),
('ter-sie-ys71#5712', 'termometro', 'siemens', 'ys71'),
('ter-sie-ys71#5713', 'termometro', 'siemens', 'ys71'),
('ter-tex-pd10#2689', 'termometro', 'texas instrument', 'pd10');



-- Creazione table dato rilevazione

DROP TABLE IF EXISTS datorilevazione;
CREATE TABLE IF NOT EXISTS datorilevazione (
  codicesensore varchar(32) NOT NULL,
  valore int(6) NOT NULL,
  flagerrore tinyint(1) NOT NULL,
  data timestamp NOT NULL,
  messaggio varchar(64) NOT NULL,
  PRIMARY KEY (codicesensore, data),
  FOREIGN KEY (codicesensore) REFERENCES datoinstallazione(codicesensore) ON DELETE CASCADE ON UPDATE CASCADE
);



-- Creazione table trasferimento automatico

DROP TABLE IF EXISTS trasferimentoautomatico;
CREATE TABLE IF NOT EXISTS trasferimentoautomatico (
  idcliente varchar(64) NOT NULL,
  flagattivazione tinyint(1) NOT NULL,
  flagterzaparte tinyint(1) NOT NULL,
  terzaparte varchar(64),
  PRIMARY KEY (idcliente),
  FOREIGN KEY (idcliente) REFERENCES utente(email) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Popolamento table trasferimento automatico

INSERT INTO trasferimentoautomatico(idcliente, flagattivazione, flagterzaparte) VALUES
('ivanlamparelli@hotmail.it', 0, 0),
('lamparelli.ivan@gmail.com', 1, 0),
('scay93@hotmail.it', 0, 0),
('mattuzz93@gmail.com', 0, 0),
('scay93g@gmail.com', 0, 0);



-- Creazione table trasferimento personalizzato

DROP TABLE IF EXISTS trasferimentopersonalizzato;
CREATE TABLE IF NOT EXISTS trasferimentopersonalizzato (
  proprietarioambiente varchar(64) NOT NULL,
  nomeambiente varchar(32) NOT NULL,
  PRIMARY KEY (proprietarioambiente, nomeambiente),
  FOREIGN KEY (proprietarioambiente) REFERENCES trasferimentoautomatico(idcliente) ON DELETE CASCADE ON UPDATE CASCADE, 
  FOREIGN KEY (proprietarioambiente, nomeambiente) REFERENCES ambiente(proprietario,nome) ON DELETE CASCADE ON UPDATE CASCADE
);



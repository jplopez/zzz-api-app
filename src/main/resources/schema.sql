create table AGENT(
  ID int not null AUTO_INCREMENT,
  AGENT_ID int not null AUTO_INCREMENT,
  NAME varchar(100) not null,
  RARITY varchar(1) not null,
  ELEMENT varchar(100) not null,
  STYLE varchar(100) not null,
  ATTACK_STYLE varchar(100) not null,
  FACTION varchar(200) not null,
  VERSION double precision,
  PRIMARY KEY ( ID )
);
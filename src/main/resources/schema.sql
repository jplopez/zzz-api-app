create table if not exists AGENT(
  ID int not null AUTO_INCREMENT,
  AGENT_ID int not null AUTO_INCREMENT,
  NAME varchar(100) not null,
  FULL_NAME varchar(200) not null,
  RARITY varchar(1) null,
  ATTRIBUTE varchar(10)  null,
  SPECIALITY varchar(10)  null,
  TYPE varchar(10)  null,
  FACTION varchar(200)  null,
  VERSION double precision  null,
  PRIMARY KEY ( ID )
);

create table if not exists SKILL(
  ID int not null AUTO_INCREMENT,
  AGENT_ID int not null,
  TYPE VARCHAR(15) NOT NULL,
  NAME varchar(200) not null,
  DESCRIPTION varchar(1000) NOT null,
  PRIMARY KEY ( ID ),
  FOREIGN KEY (AGENT_ID) REFERENCES public.AGENT(ID)
);
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

create table if not exists WENGINE(
  ID int not null AUTO_INCREMENT,
  WENGINE_ID varchar(100) not null,
  NAME varchar(100) not null,
  DESCRIPTION varchar(1000) null,
  RARITY varchar(1) null,
  BASE_ATTACK int null,
  SPECIALTY varchar(10) null,
  PRIMARY KEY ( ID )
);

create table if not exists WENGINE_SKILL_DESCRIPTIONS(
  WENGINE_ID int not null,
  SKILL_DESCRIPTIONS varchar(1000) not null,
  FOREIGN KEY (WENGINE_ID) REFERENCES public.WENGINE(ID)
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

create table if not exists BANGBOO(
  ID varchar(255) not null,
  BANGBOO_ID varchar(255) not null UNIQUE,
  NAME varchar(100) not null UNIQUE,
  RARITY varchar(1) null,
  FACTION varchar(200) null,
  PRIMARY KEY ( ID )
);

create table if not exists DISC_DRIVE_SET(
  ID varchar(255) not null,
  DISC_DRIVE_SET_ID varchar(255) not null UNIQUE,
  NAME varchar(100) not null UNIQUE,
  DESCRIPTION varchar(1000) null,
  TWO_PIECE_SKILL_DESCRIPTION varchar(1000) null,
  FOUR_PIECE_SKILL_DESCRIPTION varchar(1000) null,
  PRIMARY KEY ( ID )
);

create table if not exists DISC_DRIVE(
  ID varchar(255) not null,
  DISC_DRIVE_ID varchar(255) not null UNIQUE,
  POSITION int null,
  TYPE varchar(20) null,
  BASE_VALUE float null,
  DISC_DRIVE_SET_ID varchar(255) null,
  PRIMARY KEY ( ID )
);
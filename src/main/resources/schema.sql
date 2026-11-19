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
  VERSION real null,
  PRIMARY KEY ( ID )
);

create table if not exists WENGINE(
  ID varchar(100) primary key,
  WENGINE_ID varchar(100) not null unique,
  NAME varchar(100) not null unique,
  DESCRIPTION varchar(1000) null,
  RARITY varchar(1) null,
  BASE_ATTACK int null,
  SPECIALTY varchar(10) null,
  SUB_STAT_TYPE varchar(30) null,
  SUB_STAT_BASE_VALUE real null,
  VERSION real not null default 1.0
);

create table if not exists WENGINE_SKILL_DESCRIPTIONS(
  WENGINE_ID varchar(100) not null,
  SKILL_DESCRIPTIONS varchar(1000) not null,
  FOREIGN KEY (WENGINE_ID) REFERENCES public.WENGINE(ID)
);

create table if not exists SKILL(
  ID int not null AUTO_INCREMENT,
  TYPE VARCHAR(15) NOT NULL,
  NAME varchar(200) not null,
  DESCRIPTION varchar(1000) NOT null,
  PRIMARY KEY ( ID )
);

create table if not exists AGENT_SKILL(
  ID int not null AUTO_INCREMENT,
  SKILL_ID int not null,
  AGENT_ID int not null,
  PRIMARY KEY ( ID ),
  FOREIGN KEY (SKILL_ID) REFERENCES public.SKILL(ID),
  FOREIGN KEY (AGENT_ID) REFERENCES public.AGENT(ID)
);

create table if not exists SKILL_STAT(
  ID int not null AUTO_INCREMENT,
  SKILL_ID int not null,
  LEVEL int not null CHECK (LEVEL > 0),
  TOKEN_POSITION int not null,
  TOKEN_VALUE float not null,
  PRIMARY KEY ( ID ),
  FOREIGN KEY (SKILL_ID) REFERENCES public.SKILL(ID)
);

create table if not exists BANGBOO(
  ID varchar(100) primary key,
  BANGBOO_ID varchar(100) not null unique,
  NAME varchar(100) not null unique,
  RARITY varchar(1) null,
  FACTION varchar(200) null,
  VERSION real null
);

create table if not exists BANGBOO_STAT(
  ID varchar(100) primary key,
  BANGBOO_ID varchar(100) not null references BANGBOO(ID),
  LEVEL int not null check (LEVEL between 1 and 60),
  HP real not null,
  ATK real not null,
  DEF real not null,
  CRIT_RATE real not null,
  CRIT_DAMAGE real not null,
  IMPACT real not null,
  unique (BANGBOO_ID, LEVEL)
);

create table if not exists DISC_DRIVE_SET(
  ID varchar(100) primary key,
  DISC_DRIVE_SET_ID varchar(100) not null unique,
  NAME varchar(100) not null unique,
  DESCRIPTION varchar(1000) null,
  TWO_PIECE_SKILL_DESCRIPTION varchar(1000) null,
  FOUR_PIECE_SKILL_DESCRIPTION varchar(1000) null
);

create table if not exists DISC_DRIVE(
  ID varchar(100) primary key,
  DISC_DRIVE_ID varchar(100) not null unique,
  POSITION int not null check (POSITION between 1 and 6),
  TYPE varchar(30) null,
  BASE_VALUE real null,
  DISC_DRIVE_SET_ID varchar(100) null references DISC_DRIVE_SET(ID)
);

create index if not exists DISC_DRIVE_SET_POSITION_IDX on DISC_DRIVE (DISC_DRIVE_SET_ID, POSITION);

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
  FOREIGN KEY (AGENT_ID) REFERENCES public.AGENT(ID),
  UNIQUE (AGENT_ID, SKILL_ID)
);

create table if not exists BANGBOO(
  ID varchar(100) primary key,
  BANGBOO_ID varchar(100) not null unique,
  NAME varchar(100) not null unique,
  RARITY varchar(1) null,
  FACTION varchar(200) null,
  VERSION real null
);

create table if not exists BANGBOO_SKILL(
  BANGBOO_ID varchar(100) not null references BANGBOO(ID),
  SKILL_ID int not null references SKILL(ID),
  primary key (BANGBOO_ID, SKILL_ID)
);

-- A definition belongs to exactly one base-stat owner or one specific skill.
-- Its API type is 'base' for a character or the skill's type otherwise.
create table if not exists STAT_DEFINITION(
  ID varchar(100) primary key,
  AGENT_ID int references AGENT(ID),
  BANGBOO_ID varchar(100) references BANGBOO(ID),
  SKILL_ID int references SKILL(ID),
  NAME varchar(200) not null,
  ABBR varchar(30),
  DESCRIPTION varchar(1000),
  UNIT varchar(12) not null check (UNIT in ('number', 'percentage', 'seconds')),
  TOKEN_POSITION int,
  check (
    (AGENT_ID is not null and BANGBOO_ID is null and SKILL_ID is null) or
    (AGENT_ID is null and BANGBOO_ID is not null and SKILL_ID is null) or
    (AGENT_ID is null and BANGBOO_ID is null and SKILL_ID is not null)
  ),
  check (TOKEN_POSITION is null or (SKILL_ID is not null and TOKEN_POSITION > 0)),
  unique (SKILL_ID, TOKEN_POSITION),
  unique (BANGBOO_ID, ABBR)
);

create index if not exists STAT_DEFINITION_AGENT_IDX on STAT_DEFINITION(AGENT_ID);

create table if not exists STAT_VALUE(
  ID varchar(100) default random_uuid() primary key,
  STAT_ID varchar(100) not null references STAT_DEFINITION(ID),
  LEVEL int not null check (LEVEL between 1 and 60),
  AMOUNT decimal(19, 6) not null,
  unique (STAT_ID, LEVEL)
);

-- Read-only projections keep the legacy GET routes on the same source of truth.
create view if not exists SKILL_STAT as
select v.ID, d.SKILL_ID, v.LEVEL, d.TOKEN_POSITION, v.AMOUNT as TOKEN_VALUE
from STAT_VALUE v join STAT_DEFINITION d on d.ID = v.STAT_ID
where d.SKILL_ID is not null and d.TOKEN_POSITION is not null;

-- A legacy Bangboo row requires all six base values at that level.
-- Use the ATK value's ID as the legacy row ID (preserving the seeded IDs).
create view if not exists BANGBOO_STAT as
select max(case when d.ABBR = 'ATK' then v.ID end) as ID, d.BANGBOO_ID, v.LEVEL,
       max(case when d.ABBR = 'HP' then v.AMOUNT end) as HP,
       max(case when d.ABBR = 'ATK' then v.AMOUNT end) as ATK,
       max(case when d.ABBR = 'DEF' then v.AMOUNT end) as DEF,
       max(case when d.ABBR = 'CRIT_RATE' then v.AMOUNT end) as CRIT_RATE,
       max(case when d.ABBR = 'CRIT_DMG' then v.AMOUNT end) as CRIT_DAMAGE,
       max(case when d.ABBR = 'IMPACT' then v.AMOUNT end) as IMPACT
from STAT_VALUE v join STAT_DEFINITION d on d.ID = v.STAT_ID
where d.BANGBOO_ID is not null and d.ABBR in ('HP', 'ATK', 'DEF', 'CRIT_RATE', 'CRIT_DMG', 'IMPACT')
group by d.BANGBOO_ID, v.LEVEL
having count(*) = 6;

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

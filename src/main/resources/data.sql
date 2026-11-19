------------
-- AGENTS --
------------

INSERT INTO agent (NAME, FULL_NAME, RARITY, ATTRIBUTE, SPECIALITY, TYPE, FACTION, VERSION) values
('anby', 'Anby Demara','A','ELECTRIC','STUN','SLASH','Cunning Hares', 1.0);
INSERT INTO agent (NAME, FULL_NAME, RARITY, ATTRIBUTE, SPECIALITY, TYPE, FACTION, VERSION) values
('billy', 'Billy Kid','A','PHYSICAL','ATTACK','PIERCE','Cunning Hares', 1.0);
INSERT INTO agent (NAME, FULL_NAME, RARITY, ATTRIBUTE, SPECIALITY, TYPE, FACTION, VERSION) values
('nekomata', 'Nekomata Mana','S','PHYSICAL','ATTACK','SLASH','Cunning Hares', 1.0);
INSERT INTO agent (NAME, FULL_NAME, RARITY, ATTRIBUTE, SPECIALITY, TYPE, FACTION, VERSION) values
('nicole', 'Nicole Demara','A','ETHER','SUPPORT','STRIKE','Cunning Hares', 1.0);


INSERT INTO agent (NAME, FULL_NAME, RARITY, ATTRIBUTE, SPECIALITY, TYPE, FACTION, VERSION) values
('anton', 'Anton','A','ELECTRIC','ATTACK','PIERCE','Belobog Heavy Industries', 1.0);
INSERT INTO agent (NAME, FULL_NAME, RARITY, ATTRIBUTE, SPECIALITY, TYPE, FACTION, VERSION) values
('ben', 'Ben Bigger','A','FIRE','SHIELD','STRIKE','Belobog Heavy Industries', 1.0);
INSERT INTO agent (NAME, FULL_NAME, RARITY, ATTRIBUTE, SPECIALITY, TYPE, FACTION, VERSION) values
('grace', 'Grace Howard','S','ELECTRIC','ANOMALY','PIERCE','Belobog Heavy Industries', 1.0);
INSERT INTO agent (NAME, FULL_NAME, RARITY, ATTRIBUTE, SPECIALITY, TYPE, FACTION, VERSION) values
('koleda', 'Koleda Belobog','S','FIRE','STUN','STRIKE','Belobog Heavy Industries', 1.0);


INSERT INTO agent (NAME, FULL_NAME, RARITY, ATTRIBUTE, SPECIALITY, TYPE, FACTION, VERSION) values
('rina', 'Alexandrina Sebastiane','S','ELECTRIC','SUPPORT','STRIKE','Victoria Housekeeping', 1.0);
INSERT INTO agent (NAME, FULL_NAME, RARITY, ATTRIBUTE, SPECIALITY, TYPE, FACTION, VERSION) values
('corin', 'Corin Wickes','A','PHYSICAL','ATTACK','SLASH','Victoria Housekeeping', 1.0);
INSERT INTO agent (NAME, FULL_NAME, RARITY, ATTRIBUTE, SPECIALITY, TYPE, FACTION, VERSION) values
('ellen', 'Ellen Joe','S','ICE','ATTACK','SLASH','Victoria Housekeeping', 1.0);
INSERT INTO agent (NAME, FULL_NAME, RARITY, ATTRIBUTE, SPECIALITY, TYPE, FACTION, VERSION) values
('lycaon', 'Von Lycaon','S','ICE','STUN','STRIKE','Victoria Housekeeping', 1.0);


INSERT INTO agent (name, full_name, rarity, attribute, speciality, type, faction, version) values
('soldier11', 'Soldier 11','S','FIRE','ATTACK','SLASH','Obol Squad', 1.0);

INSERT INTO agent (name, full_name, rarity, attribute, speciality, type, faction, version) values
('soukaku', 'Soukaku','A','ICE','SUPPORT','SLASH','Hollow Special Operations Section 6', 1.0);


INSERT INTO agent (name, full_name, rarity, attribute, speciality, type, faction, version) values
('janedoe', 'Jaen Doe','S','PHYSICAL','ANOMALY','SLASH','Criminal Investigation Special Response Team', 1.1);
INSERT INTO agent (name, full_name, rarity, attribute, speciality, type, faction, version) values
('zhuyuan', 'Zhu Yuan','S','ETHER','ATTACK','PIERCE','Criminal Investigation Special Response Team', 1.0);
INSERT INTO agent (name, full_name, rarity, attribute, speciality, type, faction, version) values
('qingyi', 'Qingyi','S','ELECTRIC','STUN','STRIKE','Criminal Investigation Special Response Team', 1.1);
INSERT INTO agent (name, full_name, rarity, attribute, speciality, type, faction, version) values
('seth', 'Seth Lowell','A','ELECTRIC','SHIELD','SLASH','Criminal Investigation Special Response Team', 1.1);


INSERT INTO agent (name, full_name, rarity, attribute, speciality, type, faction, version) values
('lucy', 'Luciana de Montefio','A','FIRE','SUPPORT','STRIKE','Sons of Calydon', 1.0);
INSERT INTO agent (name, full_name, rarity, attribute, speciality, type, faction, version) values
('piper', 'Piper Wheel','A','PHYSICAL','ANOMALY','SLASH','Sons of Calydon', 1.0);
INSERT INTO agent (name, full_name, rarity, attribute, speciality, type, faction, version) values
('caesar', 'Caesar King','S','PHYSICAL','ANOMALY','STRIKE','Sons of Calydon', 1.2);
INSERT INTO agent (name, full_name, rarity, attribute, speciality, type, faction, version) values
('burnice', 'BurnICE White','S','FIRE','ANOMALY','PIERCE','Sons of Calydon', 1.2);


INSERT INTO agent (name, full_name, rarity, attribute, speciality, faction, version) values
('lighter', 'Lighter','S','FIRE','STUN' ,'Sons of Calydon', 1.3);
INSERT INTO agent (name, full_name, rarity, attribute, speciality, faction, version) values
('yanagi', 'Tsukishiro Yanagi','S','ELECTRIC','ANOMALY','Hollow Special Operations Section 6', 1.3);


------------
-- SKILLS --
------------

-- Sample skills with tokenized descriptions
INSERT INTO SKILL (TYPE, NAME, DESCRIPTION) VALUES 
('basic', 'Turbo Volt', 'Unleashes up to {1} slashes dealing {2}% damage');
INSERT INTO SKILL (TYPE, NAME, DESCRIPTION) VALUES 
('basic', 'Thunderbolt', 'Deals {1}% Electric DMG with {2} second stun');
INSERT INTO SKILL (TYPE, NAME, DESCRIPTION) VALUES 
('special', 'Fork Lightning', 'Upward slash dealing {1}% Electric DMG with {2}% crit chance');
INSERT INTO SKILL (TYPE, NAME, DESCRIPTION) VALUES 
('EX', 'Lightning Bolt', 'Powerful slash dealing {1}% Electric DMG');
INSERT INTO SKILL (TYPE, NAME, DESCRIPTION) VALUES 
('dodge', 'Slide', 'Rapid dodge with {1}% movement speed for {2} seconds');
INSERT INTO SKILL (TYPE, NAME, DESCRIPTION) VALUES 
('dash', 'Taser Blast', 'Slashes dealing {1}% Physical DMG');
INSERT INTO SKILL (TYPE, NAME, DESCRIPTION) VALUES 
('counter', 'Thunderclap', 'Counter attack dealing {1}% Electric DMG');
INSERT INTO SKILL (TYPE, NAME, DESCRIPTION) VALUES 
('chain', 'Electro Engine', 'Chain attack dealing {1}% massive Electric DMG');
INSERT INTO SKILL (TYPE, NAME, DESCRIPTION) VALUES 
('ultimate', 'Overdrive Engine', 'Ultimate dealing {1}% Electric DMG over {2} hits');
INSERT INTO SKILL (TYPE, NAME, DESCRIPTION) VALUES 
('assist', 'Thunderfall', 'Assist attack dealing {1}% Electric DMG');

--------------------
-- AGENT_SKILL --
--------------------

-- Link Anby's skills to her agent ID
SET @anby_agent_id = (SELECT ID FROM AGENT WHERE NAME = 'anby');
SET @skill1_id = (SELECT ID FROM SKILL WHERE NAME = 'Turbo Volt');
SET @skill2_id = (SELECT ID FROM SKILL WHERE NAME = 'Thunderbolt');
SET @skill3_id = (SELECT ID FROM SKILL WHERE NAME = 'Fork Lightning');
SET @skill4_id = (SELECT ID FROM SKILL WHERE NAME = 'Lightning Bolt');
SET @skill5_id = (SELECT ID FROM SKILL WHERE NAME = 'Slide');

INSERT INTO AGENT_SKILL (SKILL_ID, AGENT_ID) VALUES (@skill1_id, @anby_agent_id);
INSERT INTO AGENT_SKILL (SKILL_ID, AGENT_ID) VALUES (@skill2_id, @anby_agent_id);
INSERT INTO AGENT_SKILL (SKILL_ID, AGENT_ID) VALUES (@skill3_id, @anby_agent_id);
INSERT INTO AGENT_SKILL (SKILL_ID, AGENT_ID) VALUES (@skill4_id, @anby_agent_id);
INSERT INTO AGENT_SKILL (SKILL_ID, AGENT_ID) VALUES (@skill5_id, @anby_agent_id);

-- Illustrative base stats for Anby. Values are stored as recorded, not extrapolated.
INSERT INTO STAT_DEFINITION (ID, AGENT_ID, NAME, ABBR, UNIT) VALUES
('anby-atk', @anby_agent_id, 'Attack', 'ATK', 'number'),
('anby-def', @anby_agent_id, 'Defense', 'DEF', 'number'),
('anby-hp', @anby_agent_id, 'Health', 'HP', 'number'),
('anby-crit-rate', @anby_agent_id, 'Crit Rate', 'CRIT_RATE', 'percentage'),
('anby-crit-dmg', @anby_agent_id, 'Crit Damage', 'CRIT_DMG', 'percentage');

INSERT INTO STAT_VALUE (STAT_ID, LEVEL, AMOUNT) VALUES
('anby-atk', 1, 10), ('anby-atk', 2, 12), ('anby-atk', 3, 14),
('anby-def', 1, 8), ('anby-def', 2, 9), ('anby-def', 3, 10),
('anby-hp', 1, 100), ('anby-hp', 2, 110), ('anby-hp', 3, 120),
('anby-crit-rate', 1, 5), ('anby-crit-rate', 2, 5), ('anby-crit-rate', 3, 5),
('anby-crit-dmg', 1, 50), ('anby-crit-dmg', 2, 50), ('anby-crit-dmg', 3, 50);

-- A skill stat explicitly maps to one token in a specific skill description.
INSERT INTO STAT_DEFINITION (ID, SKILL_ID, NAME, DESCRIPTION, UNIT, TOKEN_POSITION) VALUES
('turbo-volt-slashes', @skill1_id, 'Slash count', null, 'number', 1),
('turbo-volt-damage', @skill1_id, 'Damage multiplier', null, 'percentage', 2),
('thunderbolt-damage', @skill2_id, 'Electric damage multiplier', null, 'percentage', 1),
('thunderbolt-stun-time', @skill2_id, 'Stun duration', 'How long the target remains stunned.', 'seconds', 2),
('fork-lightning-damage', @skill3_id, 'Electric damage multiplier', null, 'percentage', 1),
('fork-lightning-crit-chance', @skill3_id, 'Crit chance', null, 'percentage', 2);

INSERT INTO STAT_VALUE (ID, STAT_ID, LEVEL, AMOUNT) VALUES
('1', 'turbo-volt-slashes', 1, 4),
('2', 'turbo-volt-damage', 1, 150),
('3', 'turbo-volt-slashes', 2, 4),
('4', 'turbo-volt-damage', 2, 175),
('5', 'turbo-volt-slashes', 3, 4),
('6', 'turbo-volt-damage', 3, 200),
('7', 'thunderbolt-damage', 1, 120),
('8', 'thunderbolt-stun-time', 1, 2),
('9', 'thunderbolt-damage', 2, 140),
('10', 'thunderbolt-stun-time', 2, 2.5),
('11', 'thunderbolt-damage', 3, 160),
('12', 'thunderbolt-stun-time', 3, 3),
('13', 'fork-lightning-damage', 1, 180),
('14', 'fork-lightning-crit-chance', 1, 15),
('15', 'fork-lightning-damage', 2, 220),
('16', 'fork-lightning-crit-chance', 2, 20);

-------------
-- WENGINES --
-------------

INSERT INTO WENGINE (ID, WENGINE_ID, NAME, DESCRIPTION, RARITY, BASE_ATTACK, SPECIALTY) VALUES
('1', '1', 'Street Superstar', 'A versatile W-Engine that boosts attack damage and skill performance.', 'S', 594, 'ATTACK');
INSERT INTO WENGINE (ID, WENGINE_ID, NAME, DESCRIPTION, RARITY, BASE_ATTACK, SPECIALTY) VALUES
('2', '2', 'Cannon Roamer', 'A W-Engine designed for Anomaly agents, enhancing their anomaly buildup capabilities.', 'S', 624, 'ANOMALY');
INSERT INTO WENGINE (ID, WENGINE_ID, NAME, DESCRIPTION, RARITY, BASE_ATTACK, SPECIALTY) VALUES
('3', '3', 'Steam Oven', 'A support-oriented W-Engine that enhances team capabilities.', 'A', 463, 'SUPPORT');
INSERT INTO WENGINE (ID, WENGINE_ID, NAME, DESCRIPTION, RARITY, BASE_ATTACK, SPECIALTY) VALUES
('4', '4', 'Demara Battery Mark II', 'An enhanced version of the original Demara Battery, providing increased energy efficiency.', 'A', 476, 'STUN');
INSERT INTO WENGINE (ID, WENGINE_ID, NAME, DESCRIPTION, RARITY, BASE_ATTACK, SPECIALTY) VALUES
('5', '5', 'Rainforest Gourmet', 'A unique W-Engine designed for support agents with healing capabilities.', 'S', 624, 'SUPPORT');

SET @wengine_id = (SELECT ID FROM WENGINE WHERE NAME = 'Street Superstar');
INSERT INTO WENGINE_SKILL_DESCRIPTIONS (WENGINE_ID, SKILL_DESCRIPTIONS) VALUES 
(@wengine_id, 'Increases ATK by 25%.');
INSERT INTO WENGINE_SKILL_DESCRIPTIONS (WENGINE_ID, SKILL_DESCRIPTIONS) VALUES 
(@wengine_id, 'When the equipping character uses a Basic Attack, Dodge Counter, or Assist Attack, their DMG increases by 20% for 6s.');

SET @wengine_id = (SELECT ID FROM WENGINE WHERE NAME = 'Cannon Roamer');
INSERT INTO WENGINE_SKILL_DESCRIPTIONS (WENGINE_ID, SKILL_DESCRIPTIONS) VALUES 
(@wengine_id, 'Increases ATK by 20%.');
INSERT INTO WENGINE_SKILL_DESCRIPTIONS (WENGINE_ID, SKILL_DESCRIPTIONS) VALUES 
(@wengine_id, 'When the equipped character inflicts an Attribute Anomaly or triggers a Disorder, their ATK increases by 25% for 8s.');

SET @wengine_id = (SELECT ID FROM WENGINE WHERE NAME = 'Steam Oven');
INSERT INTO WENGINE_SKILL_DESCRIPTIONS (WENGINE_ID, SKILL_DESCRIPTIONS) VALUES 
(@wengine_id, 'Increases ATK by 15%.');
INSERT INTO WENGINE_SKILL_DESCRIPTIONS (WENGINE_ID, SKILL_DESCRIPTIONS) VALUES 
(@wengine_id, 'When the equipped character uses an EX Special Attack, all squad members'' DMG increases by 10% for 10s.');

--------------
-- BANGBOOS --
--------------

INSERT INTO BANGBOO (ID, BANGBOO_ID, NAME, RARITY, FACTION, VERSION) VALUES
('1', '1', 'Amillion', 'S', 'Victoria Housekeeping', 1.0);
INSERT INTO BANGBOO (ID, BANGBOO_ID, NAME, RARITY, FACTION, VERSION) VALUES
('2', '2', 'Butler', 'A', 'Victoria Housekeeping', 1.0);
INSERT INTO BANGBOO (ID, BANGBOO_ID, NAME, RARITY, FACTION, VERSION) VALUES
('3', '3', 'Pengboo', 'A', 'Belobog Heavy Industries', 1.0);
INSERT INTO BANGBOO (ID, BANGBOO_ID, NAME, RARITY, FACTION, VERSION) VALUES
('4', '4', 'Bagboo', 'A', 'Cunning Hares', 1.0);
INSERT INTO BANGBOO (ID, BANGBOO_ID, NAME, RARITY, FACTION, VERSION) VALUES
('5', '5', 'Electroboo', 'B', null, 1.0);
INSERT INTO BANGBOO (ID, BANGBOO_ID, NAME, RARITY, FACTION, VERSION) VALUES
('6', '6', 'Magnetiboo', 'B', null, 1.0);
INSERT INTO BANGBOO (ID, BANGBOO_ID, NAME, RARITY, FACTION, VERSION) VALUES
('7', '7', 'Rocketboo', 'B', null, 1.0);
INSERT INTO BANGBOO (ID, BANGBOO_ID, NAME, RARITY, FACTION, VERSION) VALUES
('8', '8', 'Boollseye', 'S', 'Criminal Investigation Special Response Team', 1.1);
INSERT INTO BANGBOO (ID, BANGBOO_ID, NAME, RARITY, FACTION, VERSION) VALUES
('9', '9', 'Officer Cui', 'A', 'Criminal Investigation Special Response Team', 1.1);
INSERT INTO BANGBOO (ID, BANGBOO_ID, NAME, RARITY, FACTION, VERSION) VALUES
('10', '10', 'Crybaby', 'S', 'Sons of Calydon', 1.2);
INSERT INTO BANGBOO (ID, BANGBOO_ID, NAME, RARITY, FACTION, VERSION) VALUES
('11', '11', 'Luckyboo', 'A', 'Sons of Calydon', 1.2);

-- Illustrative Bangboo data for Amillion; other Bangboos have no seeded progression.
SET @amillion_id = (SELECT ID FROM BANGBOO WHERE NAME = 'Amillion');

INSERT INTO STAT_DEFINITION (ID, BANGBOO_ID, NAME, ABBR, UNIT) VALUES
('amillion-atk', @amillion_id, 'Attack', 'ATK', 'number'),
('amillion-def', @amillion_id, 'Defense', 'DEF', 'number'),
('amillion-hp', @amillion_id, 'Health', 'HP', 'number'),
('amillion-crit-rate', @amillion_id, 'Crit Rate', 'CRIT_RATE', 'percentage'),
('amillion-crit-dmg', @amillion_id, 'Crit Damage', 'CRIT_DMG', 'percentage'),
('amillion-impact', @amillion_id, 'Impact', 'IMPACT', 'number');

INSERT INTO STAT_VALUE (ID, STAT_ID, LEVEL, AMOUNT) VALUES
('amillion-1', 'amillion-atk', 1, 25),
('amillion-2', 'amillion-atk', 2, 27),
('amillion-3', 'amillion-atk', 3, 29);
INSERT INTO STAT_VALUE (STAT_ID, LEVEL, AMOUNT) VALUES
('amillion-def', 1, 15), ('amillion-def', 2, 17), ('amillion-def', 3, 18),
('amillion-hp', 1, 100), ('amillion-hp', 2, 108), ('amillion-hp', 3, 116),
('amillion-crit-rate', 1, 5), ('amillion-crit-rate', 2, 5), ('amillion-crit-rate', 3, 5),
('amillion-crit-dmg', 1, 50), ('amillion-crit-dmg', 2, 50), ('amillion-crit-dmg', 3, 50),
('amillion-impact', 1, 30), ('amillion-impact', 2, 30), ('amillion-impact', 3, 30);

INSERT INTO SKILL (TYPE, NAME, DESCRIPTION) VALUES
('active', 'Pocket Barrage', 'Fires a burst for {1}% Physical DMG across {2} hits.'),
('additional', 'Hare Support', 'Increases the damage of Pocket Barrage by {1}% when the condition is met.'),
('chain', 'Covering Fire', 'Deals {1}% Physical DMG with a Chain Attack.');

SET @amillion_active = (SELECT ID FROM SKILL WHERE NAME = 'Pocket Barrage');
SET @amillion_additional = (SELECT ID FROM SKILL WHERE NAME = 'Hare Support');
SET @amillion_chain = (SELECT ID FROM SKILL WHERE NAME = 'Covering Fire');
INSERT INTO BANGBOO_SKILL (BANGBOO_ID, SKILL_ID) VALUES
(@amillion_id, @amillion_active), (@amillion_id, @amillion_additional), (@amillion_id, @amillion_chain);

INSERT INTO STAT_DEFINITION (ID, SKILL_ID, NAME, UNIT, TOKEN_POSITION) VALUES
('pocket-barrage-damage', @amillion_active, 'Physical damage multiplier', 'percentage', 1),
('pocket-barrage-hits', @amillion_active, 'Hit count', 'number', 2),
('hare-support-bonus', @amillion_additional, 'Damage bonus', 'percentage', 1),
('covering-fire-damage', @amillion_chain, 'Physical damage multiplier', 'percentage', 1);

INSERT INTO STAT_VALUE (STAT_ID, LEVEL, AMOUNT) VALUES
('pocket-barrage-damage', 1, 80), ('pocket-barrage-damage', 2, 90), ('pocket-barrage-damage', 3, 100),
('pocket-barrage-hits', 1, 3), ('pocket-barrage-hits', 2, 3), ('pocket-barrage-hits', 3, 3),
('hare-support-bonus', 1, 10), ('hare-support-bonus', 2, 12.5),
('covering-fire-damage', 1, 120), ('covering-fire-damage', 2, 140), ('covering-fire-damage', 3, 160);

-- Sample Drive Disc catalog. Values are illustrative, not a full progression dataset.
INSERT INTO DISC_DRIVE_SET (ID, DISC_DRIVE_SET_ID, NAME, DESCRIPTION, TWO_PIECE_SKILL_DESCRIPTION, FOUR_PIECE_SKILL_DESCRIPTION) VALUES
('1', 'woodpecker-electro', 'Woodpecker Electro', 'A set for critical-hit builds.',
 'Increases CRIT Rate by 8%.', 'Critical hits with Basic Attacks, Dodge Counters, or EX Special Attacks increase ATK.'),
('2', 'swing-jazz', 'Swing Jazz', 'A set for energy regeneration and team support.',
 'Increases Energy Regen by 20%.', 'Using a Chain Attack or Ultimate increases the squad''s damage.');

INSERT INTO DISC_DRIVE (ID, DISC_DRIVE_ID, POSITION, TYPE, BASE_VALUE, DISC_DRIVE_SET_ID) VALUES
('1', 'woodpecker-electro-1', 1, 'HP', 220, '1'),
('2', 'woodpecker-electro-2', 2, 'ATK', 35, '1'),
('3', 'woodpecker-electro-3', 3, 'DEF', 15, '1'),
('4', 'woodpecker-electro-4', 4, 'CRIT_RATE', 4.8, '1'),
('5', 'woodpecker-electro-5', 5, 'ATK_PER', 7.5, '1'),
('6', 'woodpecker-electro-6', 6, 'ANOMALY_MASTERY', 9, '1'),
('7', 'swing-jazz-1', 1, 'HP', 220, '2');

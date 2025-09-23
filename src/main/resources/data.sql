------------
-- AGENTS --
------------

INSERT INTO agent (NAME, FULL_NAME, RARITY, ATTRIBUTE, SPECIALITY, TYPE, FACTION, VERSION) values
('anby', 'Anby Demara','A','electric','stun','slash','Cunning Hares', 1.0);
INSERT INTO agent (NAME, FULL_NAME, RARITY, ATTRIBUTE, SPECIALITY, TYPE, FACTION, VERSION) values
('billy', 'Billy Kid','A','physical','attack','pierce','Cunning Hares', 1.0);
INSERT INTO agent (NAME, FULL_NAME, RARITY, ATTRIBUTE, SPECIALITY, TYPE, FACTION, VERSION) values
('nekomata', 'Nekomata Mana','S','physical','attack','slash','Cunning Hares', 1.0);
INSERT INTO agent (NAME, FULL_NAME, RARITY, ATTRIBUTE, SPECIALITY, TYPE, FACTION, VERSION) values
('nicole', 'Nicole Demara','A','ether','support','strike','Cunning Hares', 1.0);


INSERT INTO agent (NAME, FULL_NAME, RARITY, ATTRIBUTE, SPECIALITY, TYPE, FACTION, VERSION) values
('anton', 'Anton','A','electric','attack','pierce','Belobog Heavy Industries', 1.0);
INSERT INTO agent (NAME, FULL_NAME, RARITY, ATTRIBUTE, SPECIALITY, TYPE, FACTION, VERSION) values
('ben', 'Ben Bigger','A','fire','shield','strike','Belobog Heavy Industries', 1.0);
INSERT INTO agent (NAME, FULL_NAME, RARITY, ATTRIBUTE, SPECIALITY, TYPE, FACTION, VERSION) values
('grace', 'Grace Howard','S','electric','anomaly','pierce','Belobog Heavy Industries', 1.0);
INSERT INTO agent (NAME, FULL_NAME, RARITY, ATTRIBUTE, SPECIALITY, TYPE, FACTION, VERSION) values
('koleda', 'Koleda Belobog','S','fire','stun','strike','Belobog Heavy Industries', 1.0);


INSERT INTO agent (NAME, FULL_NAME, RARITY, ATTRIBUTE, SPECIALITY, TYPE, FACTION, VERSION) values
('rina', 'Alexandrina Sebastiane','S','electric','support','strike','Victoria Housekeeping', 1.0);
INSERT INTO agent (NAME, FULL_NAME, RARITY, ATTRIBUTE, SPECIALITY, TYPE, FACTION, VERSION) values
('corin', 'Corin Wickes','A','physical','attack','slash','Victoria Housekeeping', 1.0);
INSERT INTO agent (NAME, FULL_NAME, RARITY, ATTRIBUTE, SPECIALITY, TYPE, FACTION, VERSION) values
('ellen', 'Ellen Joe','S','ICE','attack','slash','Victoria Housekeeping', 1.0);
INSERT INTO agent (NAME, FULL_NAME, RARITY, ATTRIBUTE, SPECIALITY, TYPE, FACTION, VERSION) values
('lycaon', 'Von Lycaon','S','ICE','stun','strike','Victoria Housekeeping', 1.0);


INSERT INTO agent (name, full_name, rarity, attribute, speciality, type, faction, version) values
('soldier11', 'Soldier 11','S','fire','attack','slash','Obol Squad', 1.0);

INSERT INTO agent (name, full_name, rarity, attribute, speciality, type, faction, version) values
('soukaku', 'Soukaku','A','ICE','support','slash','Hollow Special Operations Section 6', 1.0);


INSERT INTO agent (name, full_name, rarity, attribute, speciality, type, faction, version) values
('janedoe', 'Jaen Doe','S','physical','anomaly','slash','Criminal Investigation Special Response Team', 1.1);
INSERT INTO agent (name, full_name, rarity, attribute, speciality, type, faction, version) values
('zhuyuan', 'Zhu Yuan','S','ether','attack','pierce','Criminal Investigation Special Response Team', 1.0);
INSERT INTO agent (name, full_name, rarity, attribute, speciality, type, faction, version) values
('qingyi', 'Qingyi','S','electric','stun','strike','Criminal Investigation Special Response Team', 1.1);
INSERT INTO agent (name, full_name, rarity, attribute, speciality, type, faction, version) values
('seth', 'Seth Lowell','A','electric','shield','slash','Criminal Investigation Special Response Team', 1.1);


INSERT INTO agent (name, full_name, rarity, attribute, speciality, type, faction, version) values
('lucy', 'Luciana de Montefio','A','fire','support','strike','Sons of Calydon', 1.0);
INSERT INTO agent (name, full_name, rarity, attribute, speciality, type, faction, version) values
('piper', 'Piper Wheel','A','physical','anomaly','slash','Sons of Calydon', 1.0);
INSERT INTO agent (name, full_name, rarity, attribute, speciality, type, faction, version) values
('caesar', 'Caesar King','S','physical','anomaly','strike','Sons of Calydon', 1.2);
INSERT INTO agent (name, full_name, rarity, attribute, speciality, type, faction, version) values
('burnice', 'BurnICE White','S','fire','anomaly','pierce','Sons of Calydon', 1.2);


INSERT INTO agent (name, full_name, rarity, attribute, speciality, faction, version) values
('lighter', 'Lighter','S','fire','stun' ,'Sons of Calydon', 1.3);
INSERT INTO agent (name, full_name, rarity, attribute, speciality, faction, version) values
('yanagi', 'Tsukishiro Yanagi','S','electric','anomaly','Hollow Special Operations Section 6', 1.3);


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

-------------------
-- SKILL_STAT --
-------------------

-- Skill stats for Turbo Volt (levels 1-3)
INSERT INTO SKILL_STAT (SKILL_ID, LEVEL, TOKEN_POSITION, TOKEN_VALUE) VALUES (@skill1_id, 1, 1, 4);
INSERT INTO SKILL_STAT (SKILL_ID, LEVEL, TOKEN_POSITION, TOKEN_VALUE) VALUES (@skill1_id, 1, 2, 150);
INSERT INTO SKILL_STAT (SKILL_ID, LEVEL, TOKEN_POSITION, TOKEN_VALUE) VALUES (@skill1_id, 2, 1, 4);
INSERT INTO SKILL_STAT (SKILL_ID, LEVEL, TOKEN_POSITION, TOKEN_VALUE) VALUES (@skill1_id, 2, 2, 175);
INSERT INTO SKILL_STAT (SKILL_ID, LEVEL, TOKEN_POSITION, TOKEN_VALUE) VALUES (@skill1_id, 3, 1, 4);
INSERT INTO SKILL_STAT (SKILL_ID, LEVEL, TOKEN_POSITION, TOKEN_VALUE) VALUES (@skill1_id, 3, 2, 200);

-- Skill stats for Thunderbolt (levels 1-3)
INSERT INTO SKILL_STAT (SKILL_ID, LEVEL, TOKEN_POSITION, TOKEN_VALUE) VALUES (@skill2_id, 1, 1, 120);
INSERT INTO SKILL_STAT (SKILL_ID, LEVEL, TOKEN_POSITION, TOKEN_VALUE) VALUES (@skill2_id, 1, 2, 2);
INSERT INTO SKILL_STAT (SKILL_ID, LEVEL, TOKEN_POSITION, TOKEN_VALUE) VALUES (@skill2_id, 2, 1, 140);
INSERT INTO SKILL_STAT (SKILL_ID, LEVEL, TOKEN_POSITION, TOKEN_VALUE) VALUES (@skill2_id, 2, 2, 2.5);
INSERT INTO SKILL_STAT (SKILL_ID, LEVEL, TOKEN_POSITION, TOKEN_VALUE) VALUES (@skill2_id, 3, 1, 160);
INSERT INTO SKILL_STAT (SKILL_ID, LEVEL, TOKEN_POSITION, TOKEN_VALUE) VALUES (@skill2_id, 3, 2, 3);

-- Skill stats for Fork Lightning (levels 1-2)
INSERT INTO SKILL_STAT (SKILL_ID, LEVEL, TOKEN_POSITION, TOKEN_VALUE) VALUES (@skill3_id, 1, 1, 180);
INSERT INTO SKILL_STAT (SKILL_ID, LEVEL, TOKEN_POSITION, TOKEN_VALUE) VALUES (@skill3_id, 1, 2, 15);
INSERT INTO SKILL_STAT (SKILL_ID, LEVEL, TOKEN_POSITION, TOKEN_VALUE) VALUES (@skill3_id, 2, 1, 220);
INSERT INTO SKILL_STAT (SKILL_ID, LEVEL, TOKEN_POSITION, TOKEN_VALUE) VALUES (@skill3_id, 2, 2, 20);

-------------
-- WENGINES --
-------------

INSERT INTO WENGINE (WENGINE_ID, NAME, DESCRIPTION, RARITY, BASE_ATTACK, SPECIALTY) VALUES
('1', 'Street Superstar', 'A versatile W-Engine that boosts attack damage and skill performance.', 'S', 594, 'ATTACK');
INSERT INTO WENGINE (WENGINE_ID, NAME, DESCRIPTION, RARITY, BASE_ATTACK, SPECIALTY) VALUES
('2', 'Cannon Roamer', 'A W-Engine designed for Anomaly agents, enhancing their anomaly buildup capabilities.', 'S', 624, 'ANOMALY');
INSERT INTO WENGINE (WENGINE_ID, NAME, DESCRIPTION, RARITY, BASE_ATTACK, SPECIALTY) VALUES
('3', 'Steam Oven', 'A support-oriented W-Engine that enhances team capabilities.', 'A', 463, 'SUPPORT');
INSERT INTO WENGINE (WENGINE_ID, NAME, DESCRIPTION, RARITY, BASE_ATTACK, SPECIALTY) VALUES
('4', 'Demara Battery Mark II', 'An enhanced version of the original Demara Battery, providing increased energy efficiency.', 'A', 476, 'STUN');
INSERT INTO WENGINE (WENGINE_ID, NAME, DESCRIPTION, RARITY, BASE_ATTACK, SPECIALTY) VALUES
('5', 'Rainforest Gourmet', 'A unique W-Engine designed for support agents with healing capabilities.', 'S', 624, 'SUPPORT');

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

INSERT INTO BANGBOO (BANGBOO_ID, NAME, RARITY, FACTION, VERSION) VALUES
('1', 'Amillion', 'S', 'Victoria Housekeeping', 1.0);
INSERT INTO BANGBOO (BANGBOO_ID, NAME, RARITY, FACTION, VERSION) VALUES
('2', 'Butler', 'A', 'Victoria Housekeeping', 1.0);
INSERT INTO BANGBOO (BANGBOO_ID, NAME, RARITY, FACTION, VERSION) VALUES
('3', 'Pengboo', 'A', 'Belobog Heavy Industries', 1.0);
INSERT INTO BANGBOO (BANGBOO_ID, NAME, RARITY, FACTION, VERSION) VALUES
('4', 'Bagboo', 'A', 'Cunning Hares', 1.0);
INSERT INTO BANGBOO (BANGBOO_ID, NAME, RARITY, FACTION, VERSION) VALUES
('5', 'Electroboo', 'B', null, 1.0);
INSERT INTO BANGBOO (BANGBOO_ID, NAME, RARITY, FACTION, VERSION) VALUES
('6', 'Magnetiboo', 'B', null, 1.0);
INSERT INTO BANGBOO (BANGBOO_ID, NAME, RARITY, FACTION, VERSION) VALUES
('7', 'Rocketboo', 'B', null, 1.0);
INSERT INTO BANGBOO (BANGBOO_ID, NAME, RARITY, FACTION, VERSION) VALUES
('8', 'Boollseye', 'S', 'Criminal Investigation Special Response Team', 1.1);
INSERT INTO BANGBOO (BANGBOO_ID, NAME, RARITY, FACTION, VERSION) VALUES
('9', 'Officer Cui', 'A', 'Criminal Investigation Special Response Team', 1.1);
INSERT INTO BANGBOO (BANGBOO_ID, NAME, RARITY, FACTION, VERSION) VALUES
('10', 'Crybaby', 'S', 'Sons of Calydon', 1.2);
INSERT INTO BANGBOO (BANGBOO_ID, NAME, RARITY, FACTION, VERSION) VALUES
('11', 'Luckyboo', 'A', 'Sons of Calydon', 1.2);

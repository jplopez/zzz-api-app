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



SET @cur_id=NULL;
SELECT ID, SET(@cur_id, ID) F FROM AGENT WHERE NAME='anby';

INSERT INTO SKILL (AGENT_ID, TYPE, NAME, DESCRIPTION) VALUES 
(@cur_id, 'basic', 'Turbo Volt','Press <IconMap:Icon_Normal> to activate.\nUnleashes up to 4 slashes. The first 3 hits deal Physical DMG the 4th hit deals Electric DMG.');
INSERT INTO SKILL (AGENT_ID, TYPE, NAME, DESCRIPTION) VALUES 
(@cur_id, 'basic', 'Thunderbolt','After unleashing the 3rd hit of the Basic Attack, hold down or pause for a short while, and then press <IconMap:Icon_Normal> to activate.\nUnleashes a downward strike that deals Electric DMG.\nAnti-Interrupt level is increased while using this skill.');
INSERT INTO SKILL (AGENT_ID, TYPE, NAME, DESCRIPTION) VALUES 
(@cur_id, 'Special', 'Fork Lightning','Press <IconMap:Icon_Special> to activate.\nUnleashes an upward slash that deals Electric DMG.\nThis attack launches faster when used after the 3rd-hit of the Basic Attack or Basic Attack: Thunderbolt.\nAnti-Interrupt level is increased while using this skill.');
INSERT INTO SKILL (AGENT_ID, TYPE, NAME, DESCRIPTION) VALUES 
(@cur_id, 'EX', 'Lightning Bolt','With enough energy, press <IconMap:Icon_SpecialReady> to activate.\nUnleashes a powerful upward slash that deals Electric DMG.\nThis attack launches faster when used after the 3rd hit of her Basic Attack or Basic Attack: Thunderbolt.\nCharacter is invulnerable while using this skill.');
INSERT INTO SKILL (AGENT_ID, TYPE, NAME, DESCRIPTION) VALUES 
(@cur_id, 'Dodge','Slide','Press <IconMap:Icon_Evade> to dodge.\nA rapid dodge.\nCharacter is invulnerable while using this skill.');
INSERT INTO SKILL (AGENT_ID, TYPE, NAME, DESCRIPTION) VALUES 
(@cur_id, 'Dash','Taser Blast','Press <IconMap:Icon_Normal> during a dodge to activate.\nSlashes all nearby enemies, dealing Physical DMG.');
INSERT INTO SKILL (AGENT_ID, TYPE, NAME, DESCRIPTION) VALUES 
(@cur_id, 'Counter','Thunderclap','Press <IconMap:Icon_Normal> during a Perfect Dodge to activate.\nSlashes enemies in front, dealing Electric DMG.\nCharacter is invulnerable while using this skill.');
INSERT INTO SKILL (AGENT_ID, TYPE, NAME, DESCRIPTION) VALUES 
(@cur_id, 'Chain','Electro Engine','When a Chain Attack is triggered, select the character to activate.\nUnleashes a powerful upwards slash at enemies in a small area, dealing massive Electric DMG.\nCharacter is invulnerable while using this skill.');
INSERT INTO SKILL (AGENT_ID, TYPE, NAME, DESCRIPTION) VALUES 
(@cur_id, 'Ultimate','Overdrive Engine','When Decibel Rating is at Maximum, press <IconMap:Icon_UltimateReady> to activate.\nUnleashes a powerful upward slash at enemies in a small area followed by several falling attacks, dealing massive Electric DMG.\nCharacter is invulnerable while using this skill.');
INSERT INTO SKILL (AGENT_ID, TYPE, NAME, DESCRIPTION) VALUES 
(@cur_id, 'QuickAssist','Thunderfall','When the on-field character is launched, press <IconMap:Icon_Switch> to activate.\nSlashes enemies in front, dealing Electric DMG.\nCharacter is invulnerable while using this skill.');
INSERT INTO SKILL (AGENT_ID, TYPE, NAME, DESCRIPTION) VALUES 
(@cur_id, 'DefensiveAssist','Flash','When the character on field is about to be attacked, press <IconMap:Icon_Switch> to activate.\nParries the enemy''s attack, inflicting massive Daze.\nCharacter is invulnerable while using this skill.');
INSERT INTO SKILL (AGENT_ID, TYPE, NAME, DESCRIPTION) VALUES 
(@cur_id, 'FollowUp','Lightning Whirl','Press <IconMap:Icon_Normal> after a Defensive Assist to activate.\nPerform a spinning slash against enemies in front, dealing Electric DMG.\nCharacter is invulnerable while using this skill.');

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

# Zenless Zone Zero Build API

ZZZ API (the API) is a cloud-based webservice containing all the information about the game Zenless Zone Zero. 
The API makes it easy for players to consult the different stats of characters (agents), weapons (w-engines) and items (discs).

Test Sample Client (Soon)

## What is Zenless Zone Zero?

Zenless Zone Zero (ZZZ) is a video game from the company Hoyoverse. Is an action RPG with a Hack'n Slash combat system. 
Players must traverse through different challenges assembling a team of 3 characters - called Agents - plus a helper robot called bangboo. 
ZZZ uses a gatcha system to collect agents and bangboos, weapons (w-engines) and gear for characters - called discs.

## Why do we need this API?

ZZZ leveling system is incredibly flexible and sophisticated, inviting players to try and experiment with different builds all the time. 
Players are constantly searching and sharing guides with recommended builds for each character. 
Although useful, these guides are usually biased towards the preferences of the creator, and general trends. 
This leads to a phenomenon called "The Meta", where a subset of agents and builds become the de-facto or 'optimal', where the majority of players gravitates towards. 
The Meta can make new and casual players feel 'left out' because they don't have those builds, and discourages the experimentation aspect of the game.

# Main Features

1. RESTFul API
2. Read-only catalogs:
    1. Agents and their skills, with level stats where recorded.
    2. W-Engines with base attack, skill descriptions, and any recorded sub-stat.
    3. Drive Discs and their sets, including 2-piece and 4-piece bonuses.
    4. Bangboos and their recorded level stats.

# REST API Documentation

This is a read-only API. Catalog collections and nested HAL collections use
`Accept: application/hal+json`; filter endpoints and the Bangboo stats list return JSON arrays.
Request HAL on detail endpoints to receive `_links`. Links point to canonical paths even
when a short alias is used. For W-Engines, Bangboos, and Discs, `{id}` refers to the
resource's `id` field, not its game/catalog identifier.

A missing resource returns `404 Not Found`. A search without matches returns `200 OK`
with `[]`. Invalid enum or numeric parameters, out-of-bounds levels or positions, and
reversed ranges on the W-Engine, Bangboo, and Disc APIs return `400 Bad Request`.

## Agents API

Base URLs: `/agents` or `/a`

### Get All Agents
**URL:** `GET /agents`  
**Accept:** `application/hal+json`  
**Description:** Returns a HATEOAS collection of all agents with self-links.

**Example Response:**
```json
{
  "_embedded": {
    "agentList": [
      {
        "id": "1",
        "agentId": "1001",
        "name": "Anby",
        "fullName": "Anby Demara",
        "version": 1.0,
        "rarity": "A",
        "attribute": "ELECTRIC",
        "speciality": "STUN",
        "type": "SLASH",
        "faction": "Cunning Hares",
        "_links": {
          "self": {
            "href": "http://localhost:8081/agents/1"
          }
        }
      }
    ]
  },
  "_links": {
    "self": {
      "href": "http://localhost:8081/agents"
    }
  }
}
```

### Get Agent by ID
**URL:** `GET /agents/{id}`  
**Parameters:** `id` (string) - Agent ID  
**Description:** Returns a specific agent by ID with HATEOAS links.

### Search Agents by Name (Exact)
**URL:** `GET /agents/name/{value}`  
**Parameters:** `value` (string) - Exact agent name (case-insensitive)  
**Description:** Returns agents matching the exact name. Useful for finding specific agents by their common name.

### Search Agents by Name (Partial)
**URL:** `GET /agents/name_like/{value}`  
**Parameters:** `value` (string) - Partial agent name  
**Description:** Returns agents containing the search term in their name. Useful for fuzzy searching.

### Search Agents by Attribute
**URL:** `GET /agents/attribute/{value}`  
**Parameters:** `value` (enum) - Agent attribute (ELECTRIC, ETHER, FIRE, ICE, PHYSICAL)  
**Description:** Returns all agents with the specified elemental attribute.

### Search Agents by Rarity
**URL:** `GET /agents/rarity/{value}`  
**Parameters:** `value` (enum) - Agent rarity (A, B, S)  
**Description:** Returns all agents of the specified rarity level.

### Search Agents by Specialty
**URL:** `GET /agents/speciality/{value}`  
**Parameters:** `value` (enum) - Agent specialty (ANOMALY, ATTACK, SHIELD, STUN, SUPPORT)  
**Description:** Returns all agents with the specified combat specialty.

### Search Agents by Type
**URL:** `GET /agents/type/{value}`  
**Parameters:** `value` (enum) - Agent type (SLASH, STRIKE, PIERCE)  
**Description:** Returns all agents with the specified damage type.

### Search Agents by Faction (Partial)
**URL:** `GET /agents/faction_like/{value}`  
**Parameters:** `value` (string) - Partial faction name  
**Description:** Returns agents whose faction contains the search term.

### Search Agents by Version (Exact)
**URL:** `GET /agents/version/{value}`  
**Parameters:** `value` (float) - Game version (e.g., 1.0, 1.1, 1.2)  
**Description:** Returns agents released in the specified game version.

### Search Agents by Version Range
**URL:** `GET /agents/version/{from}/{to}`  
**Parameters:** `from` (float), `to` (float) - Version range  
**Description:** Returns agents released between the specified versions (inclusive).

### Get Agent Skills
**URL:** `GET /agents/{agentId}/skills`  
**Accept:** `application/hal+json`  
**Parameters:** `agentId` (string) - Agent ID  
**Description:** Returns all skills for a specific agent with HATEOAS links to individual skill details.

**Example Response:**
```json
{
  "_embedded": {
    "skillList": [
      {
        "id": "skill1",
        "type": "Basic",
        "name": "Voltage Spike",
        "description": "Basic attack skill",
        "_links": {
          "self": {
            "href": "http://localhost:8081/agents/1/skills/skill1"
          },
          "skillStats": {
            "href": "http://localhost:8081/agents/1/skills/skill1"
          }
        }
      }
    ]
  },
  "_links": {
    "self": {
      "href": "http://localhost:8081/agents/1/skills"
    }
  }
}
```

### Get Agent Skill with Stats
**URL:** `GET /agents/{agentId}/skills/{skillId}`  
**Parameters:** `agentId` (string) - Agent ID, `skillId` (string) - Skill ID  
**Description:** Returns a specific skill with all its stat progression data across levels.

**Example Response:**
```json
{
  "skill": {
    "id": "skill1",
    "type": "Basic",
    "name": "Voltage Spike",
    "description": "Basic attack skill"
  },
  "skillStats": [
    {
      "id": "stat1",
      "skillId": "skill1",
      "level": 1,
      "tokenPosition": 1,
      "tokenValue": 100.0
    }
  ],
  "_links": {
    "self": {
      "href": "http://localhost:8081/agents/1/skills/skill1"
    }
  }
}
```

### Get Agent Skill Stats by Level
**URL:** `GET /agents/{agentId}/skills/{skillId}/level/{level}`  
**Parameters:** `agentId` (string) - Agent ID, `skillId` (string) - Skill ID, `level` (int) - Skill level  
**Description:** Returns skill data with stats for a specific level only.

### Get Agent Skill by ID (Deprecated)
**URL:** `GET /agents/{agentId}/skill/{skillId}`  
**Parameters:** `agentId` (string) - Agent ID, `skillId` (string) - Skill ID  
**Description:** Legacy endpoint that returns skill with stats. Use `/skills/{skillId}` instead.

**Caveats:** Agent skills have level-based stat progression with token positions and values. Some agent data may be incomplete for unreleased characters. The legacy `/skill/` endpoint (singular) is deprecated in favor of `/skills/` (plural).

## Bangboos API

Base URLs: `/bangboos` or `/b`

### Get All Bangboos
**URL:** `GET /bangboos`  
**Accept:** `application/hal+json`  
**Description:** Returns a HATEOAS collection of all bangboos with self-links.

**Example Response:**
```json
{
  "_embedded": {
    "bangbooList": [
      {
        "id": "1",
        "bangbooId": "1",
        "name": "Amillion",
        "rarity": "S",
        "faction": "Victoria Housekeeping",
        "version": 1.0,
        "_links": {
          "self": {
            "href": "http://localhost:8081/bangboos/1"
          },
          "stats": {
            "href": "http://localhost:8081/bangboos/1/stats"
          }
        }
      }
    ]
  },
  "_links": {
    "self": {
      "href": "http://localhost:8081/bangboos"
    }
  }
}
```

### Get Bangboo by ID
**URL:** `GET /bangboos/{id}`  
**Parameters:** `id` (string) - Bangboo ID  
**Description:** Returns a specific bangboo by ID with HATEOAS links.

### Search Bangboos by Name (Exact)
**URL:** `GET /bangboos/name/{value}`  
**Parameters:** `value` (string) - Exact bangboo name (case-insensitive)  
**Description:** Returns bangboos matching the exact name.

### Search Bangboos by Name (Partial)
**URL:** `GET /bangboos/name_like/{value}`  
**Parameters:** `value` (string) - Partial bangboo name  
**Description:** Returns bangboos containing the search term in their name.

### Search Bangboos by Rarity
**URL:** `GET /bangboos/rarity/{value}`  
**Parameters:** `value` (enum) - Bangboo rarity (A, B, S)  
**Description:** Returns all bangboos of the specified rarity level.

### Search Bangboos by Faction (Partial)
**URL:** `GET /bangboos/faction_like/{value}`  
**Parameters:** `value` (string) - Partial faction name  
**Description:** Returns bangboos whose faction contains the search term.

### Search Bangboos by Version (Exact)
**URL:** `GET /bangboos/version/{value}`  
**Parameters:** `value` (float) - Game version  
**Description:** Returns bangboos released in the specified game version.

### Search Bangboos by Version Range
**URL:** `GET /bangboos/version/{from}/{to}`  
**Parameters:** `from` (float), `to` (float) - Version range  
**Description:** Returns bangboos released between the specified versions (inclusive).

### Get Bangboo Stats
**URL:** `GET /bangboos/{id}/stats`  
**Parameters:** `id` (string) - Bangboo ID  
**Description:** Returns the recorded stats for a specific Bangboo, ordered by level ascending. Returns `[]` if the Bangboo exists but has no recorded stats.

**Example Response:**
```json
[
  {
    "id": "amillion-1",
    "bangbooId": "1",
    "level": 1,
    "hp": 100.0,
    "atk": 25.0,
    "def": 15.0,
    "critRate": 5.0,
    "critDamage": 50.0,
    "impact": 30.0,
    "links": [
      { "rel": "self", "href": "http://localhost:8081/bangboos/1/stats/1" }
    ]
  }
]
```

### Get Bangboo Stats by Level
**URL:** `GET /bangboos/{id}/stats/{level}`  
**Parameters:** `id` (string) - Bangboo ID, `level` (int) - Level (1-60)  
**Description:** Returns the stats for one level with `self` and `allStats` links. `prev` and `next` are included only when the immediately adjacent level is recorded.

**Caveats:** Supported levels are 1–60, but the bundled sample data contains only levels 1–3 for Amillion. A valid level without a recorded row returns `404`.

## W-Engines API

Base URLs: `/wengines` or `/w`

### Get All W-Engines
**URL:** `GET /wengines`  
**Accept:** `application/hal+json`  
**Description:** Returns a HATEOAS collection of all W-Engines with self-links.

**Example Response:**
```json
{
  "_embedded": {
    "wengineList": [
      {
        "id": "1",
        "wengineId": "1",
        "name": "Street Superstar",
        "description": "A versatile W-Engine that boosts attack damage and skill performance.",
        "rarity": "S",
        "baseAttack": 594,
        "specialty": "ATTACK",
        "skillDescriptions": [
          "Increases ATK by 25%.",
          "When the equipping character uses a Basic Attack, Dodge Counter, or Assist Attack, their DMG increases by 20% for 6s."
        ],
        "subStatType": null,
        "subStatBaseValue": null,
        "version": 1.0,
        "_links": {
          "self": {
            "href": "http://localhost:8081/wengines/1"
          }
        }
      }
    ]
  },
  "_links": {
    "self": {
      "href": "http://localhost:8081/wengines"
    }
  }
}
```

### Get W-Engine by ID
**URL:** `GET /wengines/{id}`  
**Parameters:** `id` (string) - W-Engine ID  
**Description:** Returns a specific W-Engine by ID with HATEOAS links.

### Search W-Engines by Name (Exact)
**URL:** `GET /wengines/name/{value}`  
**Parameters:** `value` (string) - Exact W-Engine name (case-insensitive)  
**Description:** Returns W-Engines matching the exact name.

### Search W-Engines by Name (Partial)
**URL:** `GET /wengines/name_like/{value}`  
**Parameters:** `value` (string) - Partial W-Engine name  
**Description:** Returns W-Engines containing the search term in their name.

### Search W-Engines by Rarity
**URL:** `GET /wengines/rarity/{value}`  
**Parameters:** `value` (enum) - W-Engine rarity (A, B, S)  
**Description:** Returns all W-Engines of the specified rarity level.

### Search W-Engines by Specialty
**URL:** `GET /wengines/specialty/{value}`  
**Parameters:** `value` (enum) - W-Engine specialty (ANOMALY, ATTACK, SHIELD, STUN, SUPPORT)  
**Description:** Returns all W-Engines designed for the specified specialty.

### Search W-Engines by Base Attack
**URL:** `GET /wengines/base_attack/{value}`  
**Parameters:** `value` (integer) - Base attack value  
**Description:** Returns W-Engines with the exact base attack value.

### Search W-Engines by Base Attack Range
**URL:** `GET /wengines/base_attack_between/{from}/{to}`  
**Parameters:** `from` (integer), `to` (integer) - Attack range  
**Description:** Returns W-Engines with base attack values between the specified range (inclusive).

### Search W-Engines by Description
**URL:** `GET /wengines/description_like/{value}`  
**Parameters:** `value` (string) - Search term  
**Description:** Returns W-Engines whose description contains the search term.

### Search W-Engines by Version (Exact)
**URL:** `GET /wengines/version/{value}`  
**Parameters:** `value` (float) - Game version  
**Description:** Returns W-Engines released in the specified game version.

### Search W-Engines by Version Range
**URL:** `GET /wengines/version/{from}/{to}`  
**Parameters:** `from` (float), `to` (float) - Version range  
**Description:** Returns W-Engines released between the specified versions (inclusive).

**Caveats:** Sub-stat fields are `null` when not recorded. The catalog does not calculate level or refinement scaling.

## Drive Discs API

Base URLs: `/discdrives` (also `/dd` or `/discs`) for individual discs, and
`/discdrivesets` (also `/dds` or `/discsets`) for sets.

### Individual discs

| Endpoint | Description |
| --- | --- |
| `GET /discdrives` | HAL collection with discs under `_embedded.discDriveList`. |
| `GET /discdrives/{id}` | One disc, with a `set` link when assigned to a set. |
| `GET /discdrives/position/{value}` | Filter by slot (integer 1–6). |
| `GET /discdrives/type/{value}` | Filter by a [StatTypes](#stattypes) value, such as `CRIT_RATE`. |
| `GET /discdrives/base_value/{value}` | Filter by a numeric base stat value. |
| `GET /discdrives/base_value_between/{from}/{to}` | Filter by an inclusive numeric base stat range. |
| `GET /discdrives/set/{id}` | Filter by set database ID, ordered by position. |

Example: `GET /discdrives/4` with `Accept: application/hal+json`

```json
{
  "id": "4",
  "discDriveId": "woodpecker-electro-4",
  "position": 4,
  "type": "CRIT_RATE",
  "baseValue": 4.8,
  "discDriveSetId": "1",
  "_links": {
    "self": { "href": "http://localhost:8081/discdrives/4" },
    "set": { "href": "http://localhost:8081/discdrivesets/1" }
  }
}
```

### Disc sets

| Endpoint | Description |
| --- | --- |
| `GET /discdrivesets` | HAL collection with sets under `_embedded.discDriveSetList`. |
| `GET /discdrivesets/{id}` | One set, with its bonus descriptions and a `discs` link. |
| `GET /discdrivesets/{id}/discs` | HAL collection of discs in the set, ordered by position. A nonexistent set returns `404`. |
| `GET /discdrivesets/name/{value}` | Exact set name, case-insensitive. |
| `GET /discdrivesets/name_like/{value}` | Partial set name, case-insensitive. |
| `GET /discdrivesets/description_like/{value}` | Partial set description, case-insensitive. |
| `GET /discdrivesets/two_piece_skill_like/{value}` | Partial 2-piece bonus description, case-insensitive. |
| `GET /discdrivesets/four_piece_skill_like/{value}` | Partial 4-piece bonus description, case-insensitive. |

Example: `GET /discdrivesets/1` with `Accept: application/hal+json`

```json
{
  "id": "1",
  "discDriveSetId": "woodpecker-electro",
  "name": "Woodpecker Electro",
  "description": "A set for critical-hit builds.",
  "twoPieceSkillDescription": "Increases CRIT Rate by 8%.",
  "fourPieceSkillDescription": "Critical hits with Basic Attacks, Dodge Counters, or EX Special Attacks increase ATK.",
  "_links": {
    "self": { "href": "http://localhost:8081/discdrivesets/1" },
    "discs": { "href": "http://localhost:8081/discdrivesets/1/discs" }
  }
}
```

On a disc, `discDriveSetId` refers to the set's `id` (database ID), not its
`discDriveSetId` (catalog identifier). The bundled discs are illustrative and do not
include level progression or all possible stat rolls.

## API Enums Reference

### Attributes
Valid values for agent elemental attributes:
- `ELECTRIC` - Electric damage type
- `ETHER` - Ether damage type  
- `FIRE` - Fire damage type
- `ICE` - Ice damage type
- `PHYSICAL` - Physical damage type

### Rarity
Valid values for agent, W-Engine, and bangboo rarity:
- `A` - A-rank (4-star equivalent)
- `B` - B-rank (3-star equivalent)  
- `S` - S-rank (5-star equivalent)

### Specialties
Valid values for agent and W-Engine specialties:
- `ANOMALY` - Anomaly specialist role
- `ATTACK` - Attack/DPS specialist role
- `SHIELD` - Defense/Tank specialist role
- `STUN` - Stun/Disruption specialist role
- `SUPPORT` - Support/Healer specialist role

### Type
Valid values for agent damage types:
- `SLASH` - Slash damage type
- `STRIKE` - Strike damage type
- `PIERCE` - Pierce damage type

### StatTypes
Valid values for W-Engine sub-stats and disc drive stats:
- `ATK` - Attack (flat value)
- `HP` - Health Points (flat value)
- `DEF` - Defense (flat value)
- `ATK_PER` - Attack Percentage
- `HP_PER` - Health Points Percentage
- `DEF_PER` - Defense Percentage
- `CRIT_RATE` - Critical Hit Rate
- `CRIT_DMG` - Critical Hit Damage
- `PEN` - Penetration (flat value)
- `PEN_RATIO` - Penetration Ratio
- `IMPACT` - Impact (flat value)
- `ANOMALY_MASTERY` - Anomaly Mastery
- `ICE_DMG` - Ice Damage Bonus
- `FIRE_DMG` - Fire Damage Bonus
- `ELECTRIC_DMG` - Electric Damage Bonus
- `ETHER_DMG` - Ether Damage Bonus
- `PHYSICAL_DMG` - Physical Damage Bonus

# How to Use

Run the app with Java 17 or newer using `./mvnw spring-boot:run`. It listens on port
8081 and loads sample data into an in-memory H2 database. For example:

```sh
curl -H 'Accept: application/hal+json' http://localhost:8081/wengines
curl -H 'Accept: application/hal+json' http://localhost:8081/bangboos/1/stats/2
curl -H 'Accept: application/hal+json' http://localhost:8081/discdrivesets/1/discs
```

Run the test suite with `./mvnw test`; it does not require a separate running server.

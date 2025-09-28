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
2. Authenticated Clients
3. Datasets:
    1. Agents, their stats and active and passive skills from level 1 to 60.
    2. W-Engines with their stats from 1 to 5 starts, and level 1 to 60.
    3. Discs with their stats from 1 to 15 (S rank), 1 to 12 (A Rank), 1 to 10 (B Rank)
    4. Bangboos with their stats from 1 to 5 stars and level 1 to 60.

# REST API Documentation

This API follows HATEOAS (Hypermedia as the Engine of Application State) principles and returns JSON with `application/hal+json` content type for collection endpoints. All endpoints support both full and short path aliases.

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
            "href": "http://localhost:8080/agents/1"
          }
        }
      }
    ]
  },
  "_links": {
    "self": {
      "href": "http://localhost:8080/agents"
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
**URL:** `GET /agents/specialty/{value}`  
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

**Caveats:** Agent skills and detailed stats are referenced through separate endpoints. Some agent data may be incomplete for unreleased characters.

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
            "href": "http://localhost:8080/bangboos/1"
          }
        }
      }
    ]
  },
  "_links": {
    "self": {
      "href": "http://localhost:8080/bangboos"
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
**Description:** Returns all stat levels (1-60) for a specific bangboo, ordered by level ascending.

**Example Response:**
```json
[
  {
    "id": "stat1",
    "bangbooId": "1",
    "level": 1,
    "hp": 100.0,
    "atk": 25.0,
    "def": 15.0,
    "critRate": 5.0,
    "critDamage": 50.0,
    "impact": 30.0,
    "_links": {
      "self": {
        "href": "http://localhost:8080/bangboos/1/stats/1"
      }
    }
  }
]
```

### Get Bangboo Stats by Level
**URL:** `GET /bangboos/{id}/stats/{level}`  
**Parameters:** `id` (string) - Bangboo ID, `level` (int) - Level (1-60)  
**Description:** Returns stats for a specific bangboo at a specific level with navigation links to prev/next levels.

**Caveats:** Bangboo stats are only available for levels 1-60. Navigation links to previous/next levels are only provided when applicable.

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
        "wengineId": "w001",
        "name": "Steel Cushion",
        "description": "A reliable W-Engine for defense",
        "rarity": "A",
        "baseAttack": 42,
        "specialty": "SHIELD",
        "skillDescriptions": ["Increases DEF by 15%"],
        "subStatType": "DEF_PER",
        "subStatBaseValue": 8.0,
        "_links": {
          "self": {
            "href": "http://localhost:8080/wengines/1"
          }
        }
      }
    ]
  },
  "_links": {
    "self": {
      "href": "http://localhost:8080/wengines"
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

**Caveats:** W-Engine upgrade statistics from rank 1 to 5 are not yet implemented. Sub-stat scaling by refinement level is not available through the API.

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

The API is a Spring Boot application that can be run locally. All endpoints return JSON data with HATEOAS links for navigation. Use the short path aliases (e.g., `/a` instead of `/agents`) for more concise URLs.

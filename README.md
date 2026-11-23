# Zenless Zone Zero Build API

ZZZ API (the API) is a cloud-based webservice containing all the information about the game Zenless Zone Zero. 
The API makes it easy for players to consult the different stats of characters (agents), weapons (w-engines) and items (discs).

[Jump to the REST API index](#rest-api-documentation)

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
    1. Agents and their skills, with named base and skill stats by level.
    2. W-Engines with base attack, skill descriptions, and any recorded sub-stat.
    3. Drive Discs and their sets, including 2-piece and 4-piece bonuses.
    4. Bangboos and their skills, with the same stat progression API.
3. Stateless Drive Disc upgrades, exact model forecasts, and weighted comparisons.

# REST API Documentation

**Index**

- [Agents](#agents-api) — `/agents`
- [Bangboos](#bangboos-api) — `/bangboos`
- [W-Engines](#w-engines-api) — `/wengines`
- [Disc Drives](#disc-drives-api) — `/discdrives` and `/discdrivesets`
- [Drive Disc Simulation](#drive-disc-simulation-api) — `/v1/disc-rules`, `/v1/disc-sessions`, and `/v1/disc-comparisons`

Catalog and progression endpoints are read-only (`GET`). Simulation `POST`s do not modify the catalog or save sessions. [Run the API locally](#how-to-use) at `http://localhost:8081` to try the examples.

| Request | `Accept` header | Response |
| --- | --- | --- |
| Catalog or nested collection, such as `/agents` or `/agents/1/skills` | `application/hal+json` | HAL object with results under `_embedded` and navigation under `_links`. An empty collection may omit `_embedded`. |
| Catalog or skill detail | `application/hal+json` for `_links`, or `application/json` | One object. |
| Catalog filter | `application/json` | A JSON array; no matches returns `200 OK` with `[]`. |
| Named stats or skill stats | `application/json` | An object with a `stats` array. |

Use the resource's `id` field in paths, not its game/catalog identifier (such as
`agentId` or `wengineId`). The examples use canonical paths, but aliases also work.
A missing resource returns `404 Not Found`. Invalid inputs such as unknown enum
codes or out-of-range levels return `400 Bad Request`.

## Agents API

**Base path:** `/agents` · **Aliases:** `/a`, `/agent`

[Agent skills and stats](#agent-skills-and-stats)

### Agent catalog

| GET path | Returns |
| --- | --- |
| `/agents` | All agents in `_embedded.agentList`. |
| `/agents/{id}` | One agent's profile, including name, faction, attribute, and specialty. With HAL, follow `stats` for base stats and `allSkills` for skills. |
| `/agents/name/{value}` | Exact name, case-insensitive. |
| `/agents/name_like/{value}` | Substring of the agent's **full name**. |
| `/agents/rarity/{value}` | Rarity: `A`, `B`, or `S`. |
| `/agents/attribute/{value}` | Attribute: `ELECTRIC`, `ETHER`, `FIRE`, `ICE`, or `PHYSICAL`. |
| `/agents/speciality/{value}` | Specialty: `ANOMALY`, `ATTACK`, `SHIELD`, `STUN`, or `SUPPORT`. |
| `/agents/type/{value}` | Damage type: `SLASH`, `STRIKE`, or `PIERCE`. |
| `/agents/faction_like/{value}` | Substring of the faction name. |
| `/agents/version/{value}` | Exact game version, such as `1.2`. |
| `/agents/version/{from}/{to}` | Inclusive game version range. |

For example, `GET /agents/name/anby` finds Anby. Use the `id` from the result to request her profile or progression. The profile does not include a prose biography.

### Agent skills and stats

| GET path | Returns |
| --- | --- |
| `/agents/{id}/skills` | Skill descriptions in `_embedded.skillList` (HAL), without scaling values. Each skill has an `id`, a `type`, and a `stats` link. |
| `/agents/{id}/stats/{statType}` | All recorded levels for each named stat of this type. Use `base` for base stats. |
| `/agents/{id}/stats/{statType}/{level}` | Named stats of this type, with values only at the requested level. |
| `/agents/{id}/skills/{skillId}/stats` | One skill's named stats at all recorded levels. |
| `/agents/{id}/skills/{skillId}/stats/{level}` | One skill's named stats at a single level. |

Agent `statType` values: `base`, `basic`, `dodge`, `assist`, `ultimate`, `chain`, `special`, `ex`, `dash`, and `counter` (case-insensitive). A skill type can match multiple skills. For example, `/agents/1/stats/basic` returns stats for both of Anby's basic skills; use `skillId` to tell them apart.

**Example:** `GET /agents/1/stats/base?statId=anby-atk` returns Anby's Attack at all recorded levels:

```json
{
  "stats": [
    {
      "id": "anby-atk",
      "type": "base",
      "name": "Attack",
      "abbr": "ATK",
      "unit": "number",
      "values": [
        { "level": 1, "value": 10 },
        { "level": 2, "value": 12 },
        { "level": 3, "value": 14 }
      ]
    }
  ]
}
```

For a full agent page, fetch the profile, `/agents/{id}/stats/base`, and
`/agents/{id}/skills` in parallel. Take each skill's `id` from the skill list:
for example, `/agents/1/skills/1/stats` gets Turbo Volt's progression. To batch
requests by skill `type` instead, group the returned stats by `skillId`.

#### Named stat responses

These rules apply to both Agent and Bangboo named stat routes:

- `level` is the Agent's or Bangboo's level (an integer from 1 to 60). Omit it to get all **recorded** levels in ascending order. Missing levels are not estimated. The bundled progression for Anby and Amillion is illustrative, not a full dataset.
- Add `?statId={statId}` to any named stat route to select one stat. Use its case-sensitive `stats[].id` from a previous response. For skill-specific routes, take `skillId` from the skill list.
- Each stat has an `id`, `type`, `name`, `unit`, and `values`. `abbr` and `description` are optional. A skill stat also includes `skillId` and, when tied to a `{n}` placeholder in the skill description, a one-based `tokenPosition`.
- Units are `number` (flat amounts or counts), `percentage` (`10` means **10%**, not `0.10`), and `seconds` (durations).
- No definitions in a known scope returns `{"stats":[]}`. A known stat with no value at the requested level returns `"values":[]` for that stat. An unknown or out-of-scope `statId` or `skillId` returns `404`. An invalid type or level, or a blank `statId`, returns `400 Bad Request`.

#### Legacy Agent skill routes

These return a `skill` object plus a `skillStats` array of token values (`id`, `skillId`, `level`, `tokenPosition`, `tokenValue`). Use the named `/stats` routes above for new clients.

| GET path | Returns |
| --- | --- |
| `/agents/{id}/skills/{skillId}` | Skill metadata and token values for all recorded levels. |
| `/agents/{id}/skills/{skillId}/level/{level}` | Skill metadata and token values at one level. |
| `/agents/{id}/skill/{skillId}` | Deprecated singular route for all recorded levels. |

[Back to index](#rest-api-documentation)

## Bangboos API

**Base path:** `/bangboos` · **Aliases:** `/b`, `/bangboo`

[Bangboo skills and stats](#bangboo-skills-and-stats)

### Bangboo catalog

| GET path | Returns |
| --- | --- |
| `/bangboos` | All Bangboos in `_embedded.bangbooList`. |
| `/bangboos/{id}` | One Bangboo's profile. With HAL, `baseStats` links to named base stats, `allSkills` links to skills, and `stats` links to the legacy level rows. |
| `/bangboos/name/{value}` | Exact name, case-insensitive. |
| `/bangboos/name_like/{value}` | Substring of the name. |
| `/bangboos/rarity/{value}` | Rarity: `A`, `B`, or `S`. |
| `/bangboos/faction_like/{value}` | Substring of the faction name. |
| `/bangboos/version/{value}` | Exact game version, such as `1.2`. |
| `/bangboos/version/{from}/{to}` | Inclusive game version range. Use positive numbers in ascending order. |

### Bangboo skills and stats

| GET path | Returns |
| --- | --- |
| `/bangboos/{id}/skills` | Skill descriptions in `_embedded.skillList` (HAL), without scaling values. Each skill has a `stats` link. |
| `/bangboos/{id}/skills/{skillId}` | One skill's description. Request HAL for its `stats` link. |
| `/bangboos/{id}/stats/{statType}` | All recorded levels for each named stat of this type. Use `base` for base stats. |
| `/bangboos/{id}/stats/{statType}/{level}` | Named stats of this type, with values only at the requested level. |
| `/bangboos/{id}/skills/{skillId}/stats` | One skill's named stats at all recorded levels. |
| `/bangboos/{id}/skills/{skillId}/stats/{level}` | One skill's named stats at a single level. |

Bangboo `statType` values: `base`, `active`, `additional`, and `chain` (case-insensitive). Use `GET /bangboos/1/stats/base` for Amillion's base progression. The [named stat response rules](#named-stat-responses) also apply here, including the optional `statId` filter and level range of 1–60.

**Example:** `GET /bangboos/1/skills/11/stats/2?statId=pocket-barrage-damage` returns the damage multiplier for Pocket Barrage at Bangboo level 2:

```json
{
  "stats": [
    {
      "id": "pocket-barrage-damage",
      "type": "active",
      "name": "Physical damage multiplier",
      "unit": "percentage",
      "skillId": "11",
      "tokenPosition": 1,
      "values": [{ "level": 2, "value": 90 }]
    }
  ]
}
```

#### Legacy Bangboo level rows

The numeric path `/bangboos/{id}/stats/2` returns a legacy row, while `/bangboos/{id}/stats/base/2` returns named stats. Legacy rows require all six fields (`hp`, `atk`, `def`, `critRate`, `critDamage`, `impact`) at that level. Use named stats if you also want partial levels.

| GET path | Returns |
| --- | --- |
| `/bangboos/{id}/stats` | A JSON array of complete rows in level order, or `[]` when none are complete. |
| `/bangboos/{id}/stats/{level}` | One complete row at level 1–60, or `404` if none exists. With HAL, includes `self` and `allStats`, plus `prev` and `next` when the corresponding adjacent row exists. |

[Back to index](#rest-api-documentation)

## W-Engines API

**Base path:** `/wengines` · **Alias:** `/w`

| GET path | Returns |
| --- | --- |
| `/wengines` | All W-Engines in `_embedded.wengineList`. |
| `/wengines/{id}` | One W-Engine, including base attack, skill descriptions, and any recorded sub-stat. |
| `/wengines/name/{value}` | Exact name, case-insensitive. |
| `/wengines/name_like/{value}` | Substring of the name. |
| `/wengines/rarity/{value}` | Rarity: `A`, `B`, or `S`. |
| `/wengines/specialty/{value}` | Specialty: `ANOMALY`, `ATTACK`, `SHIELD`, `STUN`, or `SUPPORT`. |
| `/wengines/base_attack/{value}` | Exact base attack (integer). |
| `/wengines/base_attack_between/{from}/{to}` | Inclusive base attack range (integers, `from` ≤ `to`). |
| `/wengines/description_like/{value}` | Substring of the description. |
| `/wengines/version/{value}` | Exact game version, such as `1.2`. |
| `/wengines/version/{from}/{to}` | Inclusive game version range. Use positive numbers in ascending order. |

**Example:** `GET /wengines/1` with `Accept: application/hal+json`:

```json
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
    "self": { "href": "http://localhost:8081/wengines/1" }
  }
}
```

Sub-stat fields are `null` when not recorded. See [stat type codes](#stat-type-codes) for `subStatType` values. The catalog does not calculate level or refinement scaling.

[Back to index](#rest-api-documentation)

## Disc Drives API

**Base paths:** `/discdrives` (aliases `/dd`, `/discs`) and `/discdrivesets` (aliases `/dds`, `/discsets`).

### Individual discs

| GET path | Returns |
| --- | --- |
| `/discdrives` | All discs in `_embedded.discDriveList`. |
| `/discdrives/{id}` | One disc. With HAL, follow `set` when the disc belongs to a set. |
| `/discdrives/position/{value}` | Discs in a slot (integer 1–6). |
| `/discdrives/type/{value}` | Discs with the given [stat type code](#stat-type-codes). |
| `/discdrives/base_value/{value}` | Exact numeric base value. |
| `/discdrives/base_value_between/{from}/{to}` | Inclusive numeric base value range (`from` ≤ `to`). |
| `/discdrives/set/{id}` | Discs in the set, ordered by position. Use the set's `id`. |

**Example:** `GET /discdrives/4` with `Accept: application/hal+json`:

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

On a disc, `discDriveSetId` refers to the set's `id`, not the set's own catalog identifier (also named `discDriveSetId`). The sample disc data does not cover level progression or all possible stat rolls.

### Disc sets

| GET path | Returns |
| --- | --- |
| `/discdrivesets` | All sets in `_embedded.discDriveSetList`. |
| `/discdrivesets/{id}` | One set, including `twoPieceSkillDescription` and `fourPieceSkillDescription`. With HAL, follow `discs` for the set's discs. |
| `/discdrivesets/{id}/discs` | HAL collection in `_embedded.discDriveList`, ordered by position. A missing set returns `404`. |
| `/discdrivesets/name/{value}` | Exact set name, case-insensitive. |
| `/discdrivesets/name_like/{value}` | Substring of the set name, case-insensitive. |
| `/discdrivesets/description_like/{value}` | Substring of the set description, case-insensitive. |
| `/discdrivesets/two_piece_skill_like/{value}` | Substring of the 2-piece bonus description, case-insensitive. |
| `/discdrivesets/four_piece_skill_like/{value}` | Substring of the 4-piece bonus description, case-insensitive. |

For example, `GET /discdrivesets/1` describes the Woodpecker Electro set; `GET /discdrivesets/1/discs` with `Accept: application/hal+json` lists its discs.

### Stat type codes

Use these values for disc `type` filters. The same codes appear as W-Engine `subStatType` values when recorded.

| Group | Codes |
| --- | --- |
| Base and percentage stats | `ATK`, `HP`, `DEF`, `ATK_PER`, `HP_PER`, `DEF_PER` |
| Combat stats | `CRIT_RATE`, `CRIT_DMG`, `PEN`, `PEN_RATIO`, `IMPACT`, `ANOMALY_MASTERY` |
| Damage bonus | `ICE_DMG`, `FIRE_DMG`, `ELECTRIC_DMG`, `ETHER_DMG`, `PHYSICAL_DMG` |

[Back to index](#rest-api-documentation)

## Drive Disc Simulation API

Import a Drive Disc, simulate or record upgrades, forecast roll outcomes, and
compare discs using stat weights. See the [Drive Disc Simulation API guide](DISC_SIMULATION_API.md)
for endpoints, worked examples, and limits.

# How to Use

Run the app with Java 17 or newer using `./mvnw spring-boot:run`. It listens on port
8081 and loads sample data into an in-memory H2 database. For example:

```sh
curl -H 'Accept: application/hal+json' http://localhost:8081/agents/1/skills
curl -H 'Accept: application/json' 'http://localhost:8081/bangboos/1/stats/base?statId=amillion-atk'
curl -H 'Accept: application/hal+json' http://localhost:8081/wengines
curl -H 'Accept: application/hal+json' http://localhost:8081/discdrivesets/1/discs
```

Run the test suite with `./mvnw test`; it does not require a separate running server.

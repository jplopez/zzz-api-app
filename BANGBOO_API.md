# Bangboo REST API with HATEOAS

This document describes the REST API endpoints for the Bangboo entity, implementing HATEOAS (Hypermedia as the Engine of Application State) following the established patterns in the ZZZ API application.

## Endpoints

### Base URLs
- `/bangboos` - Full path
- `/b` - Short path alias
- `/bangboo` - Singular path alias

### Available Operations

#### 1. Get All Bangboos
```
GET /bangboos
Accept: application/hal+json
```
Returns a HATEOAS collection of all bangboos with self-links.

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
          },
          "baseStats": {
            "href": "http://localhost:8081/bangboos/1/stats/base",
            "type": "application/json"
          },
          "allSkills": {
            "href": "http://localhost:8081/bangboos/1/skills"
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

#### 2. Get Bangboo by ID
```
GET /bangboos/{id}
```
Returns a single bangboo by its database ID.

#### 3. Search by Name (Exact Match)
```
GET /bangboos/name/{name}
```
Example: `/bangboos/name/Amillion`

#### 4. Search by Name (Partial Match)
```
GET /bangboos/name_like/{partial_name}
```
Example: `/bangboos/name_like/boo`

#### 5. Search by Rarity
```
GET /bangboos/rarity/{rarity}
```
Example: `/bangboos/rarity/S`

Valid rarity values: `A`, `B`, `S`

#### 6. Search by Faction (Partial Match)
```
GET /bangboos/faction_like/{faction}
```
Example: `/bangboos/faction_like/Victoria`

#### 7. Search by Version (Exact)
```
GET /bangboos/version/{version}
```
Example: `/bangboos/version/1.0`

#### 8. Search by Version Range
```
GET /bangboos/version/{from}/{to}
```
Example: `/bangboos/version/1.0/1.2`

#### 9. List Complete Level Stats (Legacy format)
```
GET /bangboos/{id}/stats
```
Returns levels with all six legacy base stats in ascending order, or `[]` if the
Bangboo exists but has no complete levels. The `id` in these paths is the database ID.

#### 10. Get Stats at a Level (Legacy format)
```
GET /bangboos/{id}/stats/{level}
Accept: application/hal+json
```
`level` must be an integer from 1 to 60. The response includes `self` and
`allStats` links and links to adjacent levels (`prev` and `next`) only when
those levels are recorded. A missing Bangboo or a missing eligible level returns
`404`. The bundled sample contains base stats for Amillion at levels 1–3 only.

### Atomic stats and skills

Use `/bangboos/{id}/stats/{statType}` for all recorded levels, or append `/{level}`
for a single level. Use `/bangboos/{id}/skills` to discover skills and
`/bangboos/{id}/skills/{skillId}/stats` (optionally followed by `/{level}`) for that
skill's values. You can add `?statId={statId}` to narrow any atomic stat response.
See [Bangboo skills and stats in the README](README.md#bangboo-skills-and-stats).

## Data Model

### Bangboo Entity Properties

| Property | Type | Required | Unique | Description |
|----------|------|----------|--------|-------------|
| id | String | Yes | Yes | Auto-generated database ID |
| bangbooId | String | Yes | Yes | Game-specific bangboo identifier |
| name | String | Yes | Yes | Bangboo name |
| rarity | Enum | No | No | Rarity tier (A, B, S) |
| faction | String | No | No | Associated faction |
| version | Float | No | No | Game version introduced (default: 1.0) |

### Version Utility Methods

The Bangboo entity includes utility methods for version comparison:
- `isVersion(float version)` - Check if bangboo was released in specific version
- `afterVersion(float version)` - Check if released after version
- `beforeVersion(float version)` - Check if released before version
- `wasInVersion(float version)` - Check if was present in version

## Sample Data

The database includes 11 initial bangboos:

1. **Amillion** (S-rank, Victoria Housekeeping, v1.0)
2. **Butler** (A-rank, Victoria Housekeeping, v1.0)
3. **Pengboo** (A-rank, Belobog Heavy Industries, v1.0)
4. **Bagboo** (A-rank, Cunning Hares, v1.0)
5. **Electroboo** (B-rank, no faction, v1.0)
6. **Magnetiboo** (B-rank, no faction, v1.0)
7. **Rocketboo** (B-rank, no faction, v1.0)
8. **Boollseye** (S-rank, Criminal Investigation Special Response Team, v1.1)
9. **Officer Cui** (A-rank, Criminal Investigation Special Response Team, v1.1)
10. **Crybaby** (S-rank, Sons of Calydon, v1.2)
11. **Luckyboo** (A-rank, Sons of Calydon, v1.2)

## Error Handling

The API follows standard HTTP status codes:
- `200 OK` - Successful request
- `404 Not Found` - Bangboo not found
- `400 Bad Request` - Invalid request parameters

## HATEOAS Links

All individual bangboo responses include HATEOAS `_links` with:
- `self` - Link to the specific bangboo resource
- `stats` - Link to the legacy list of complete base-stat rows by level
- `baseStats` - Link to the atomic base stats and their recorded values
- `allSkills` - Link to the description-only skill list

HAL collection responses include:
- `self` - Link to the collection endpoint

## Testing

`BangbooControllerIntegrationTest` and `BangbooControllerStatIntegrationTest`
exercise the routes against H2 with MockMvc. `BangbooStatRepositoryUnitTest`
checks level ordering, lookups, and the unique stat/level constraint.
`ProgressionIntegrationTest` covers the atomic endpoints for both resource types.

## Architecture

The Bangboo API follows the established ZZZ application patterns:

- **Entity**: `Bangboo` extends `RepresentationModel<Bangboo>` for HATEOAS
- **Repository**: `BangbooRepository` extends `JpaRepository` with custom find methods
- **Controller**: `BangbooController` extends `ZZZController` with HATEOAS link generation
- **Tests**: Comprehensive unit and integration tests following existing patterns

See the [README](README.md#bangboos-api) for additional response examples.

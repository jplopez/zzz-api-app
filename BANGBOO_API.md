# Bangboo REST API with HATEOAS

This document describes the REST API endpoints for the Bangboo entity, implementing HATEOAS (Hypermedia as the Engine of Application State) following the established patterns in the ZZZ API application.

## Endpoints

### Base URLs
- `/bangboos` - Full path
- `/b` - Short path alias

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

Collection responses include:
- `self` - Link to the collection endpoint

## Testing

The implementation includes comprehensive test coverage:

### Unit Tests (`BangbooRepositoryUnitTest`)
- All find methods testing
- Nullable column edge cases
- Constraint validation
- Enum value handling
- Version comparison methods

### Integration Tests (`BangbooControllerIntegrationTest`)
- HTTP endpoint testing
- HATEOAS response validation
- JSON serialization testing
- Error handling verification

## Architecture

The Bangboo API follows the established ZZZ application patterns:

- **Entity**: `Bangboo` extends `RepresentationModel<Bangboo>` for HATEOAS
- **Repository**: `BangbooRepository` extends `JpaRepository` with custom find methods
- **Controller**: `BangbooController` extends `ZZZController` with HATEOAS link generation
- **Tests**: Comprehensive unit and integration tests following existing patterns
- **Mock**: `BangbooMock` provides test data generation utilities

All components are fully documented with JavaDoc and follow the naming conventions established by the Agent and WEngine implementations.
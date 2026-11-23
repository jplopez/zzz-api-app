# Drive Disc Simulation API

The simulation uses versioned game data, not the disc catalog's sample rows. Keep
`response.session` on the client and send the entire object back, including the
derived `disc`. The server validates the rules and replays `history` from
`initial` on each request. It does not store or sign sessions.

| Request | Use it to |
| --- | --- |
| `GET /v1/disc-rules[?version=2.4]` | Read eligible main and substats, rarity caps, milestones, per-roll values and weights, and main-stat values indexed by level from 0. |
| `GET /v1/schemas/disc-session.json` | Read the [Draft 2020-12 JSON Schema](src/main/resources/disc-simulation/schemas/disc-session.json) for sessions and request/response bodies. |
| `POST /v1/disc-sessions` | Import a real or hypothetical disc at any legal level. An explicit `seed` makes creation repeatable. |
| `POST /v1/disc-sessions/step` | Advance to `to_level`, crossing at most one roll milestone. Return the appended events and computed disc values. |
| `POST /v1/disc-sessions/rewind` | Keep `to_event` events (0 goes back to `initial`). Set `fork: true` to start a new RNG sequence. |
| `POST /v1/disc-sessions/forecast` | Enumerate grouped outcomes through `target_level` (default: rarity cap). Returns the supplied session unchanged. |
| `POST /v1/disc-comparisons` | Score and rank 2–12 sessions or bare disc states against a common `weights` object. |

`GET /v1/openapi.yaml` serves the [OpenAPI 3.1 reference](src/main/resources/disc-simulation/openapi.yaml) with worked examples. Send `Content-Type: application/json` to the POST endpoints.

## Import a disc and take an action

This example uses `curl` and `jq`. `rolls` includes the first appearance of a
substat, so each starting substat has one roll. The stat IDs come from `/v1/disc-rules`
and differ from the catalog's enum codes.

```sh
api=http://localhost:8081
curl -fsS "$api/v1/disc-sessions" -H 'Content-Type: application/json' -d '{
  "rules_version": "2.5",
  "seed": 8856,
  "initial": {
    "rarity": "S", "slot": 4, "main_stat": "crit_rate", "level": 0,
    "substats": [
      {"stat": "atk_pct", "rolls": 1},
      {"stat": "crit_dmg", "rolls": 1},
      {"stat": "hp", "rolls": 1}
    ]
  }
}' > disc-created.json

# Execute next_roll (here, level 3), attaching the session to the action body.
action=$(jq -r '.actions.next_roll.href' disc-created.json)
jq -c '{session} + .actions.next_roll.body' disc-created.json |
  curl -fsS "$api$action" -H 'Content-Type: application/json' --data-binary @- > disc-stepped.json

# Forecast the remaining rolls using a crit-focused score.
jq -c '{session, weights: {crit_rate: 2, crit_dmg: 1}, thresholds: [60]}' disc-stepped.json |
  curl -fsS "$api/v1/disc-sessions/forecast" -H 'Content-Type: application/json' --data-binary @-
```

For any descriptor in `actions`, use its `method` and `href`, copy its `body`,
and attach the response fields listed in `send`. `next_level` advances one level;
`next_roll` applies the intervening `levels_skipped` main-stat-only levels first.
At the cap, `complete` is true and both step actions are absent. `forecast` is
always available; `undo` and `fork` are offered when there is history.

- To record a real roll, include `"observe": {"stat": "pen"}` in a step to a roll
  level. The API derives `added` or `upgraded`; an observed event consumes no draw.
- Simulated rolls use PCG32, with zero-based `draw` indices. Retrying the same
  step request returns the same body. Undo keeps the draw counter, so a new step
  after undo uses the next draw (which may still select the same stat).
- Fork keeps the selected history as recorded, resets `rng.draws` and records
  the parent seed and fork point in `meta.forked_from`. Supply a different `seed`
  or omit it to derive a repeatable one. Unlike fork, creation without a seed
  generates a fresh random seed on each request.

## Rules and scoring

The bundled [2.4](src/main/resources/disc-simulation/rules/2.4.json) and
[2.5](src/main/resources/disc-simulation/rules/2.5.json) snapshots link to their
pinned game-data archives. The API defaults to 2.5; a session always keeps its
original version. Both snapshots have B/A/S caps of 9/12/15, starting substat
counts of 1–2/2–3/3–4, and milestones every three levels. Main stats use the
archived level multipliers; substats are a fixed value per roll.

**Forecasts are exact under the uniform model, not verified in-game odds.** Each
eligible new or existing substat has equal model probability. `result.probability_model`
is `uniform`. The API groups outcomes by final roll counts and computes probabilities
without sampling or consuming RNG draws.

Scores sum each main or substat value times its supplied weight. Use nonnegative
weights keyed by stat ID; a missing weight is zero. Percentages are in points
(`24` for 24%). Weighted forecasts include each outcome's score, `expected_score`,
and probabilities of reaching any supplied `thresholds`. Comparisons return
`results` in input order, with one-based ranks by expected score at each disc's
cap, then current score, then input order. Mixed slots return `mixed-slots` in
`warnings`. Sessions retain their own rules version; `rules_version` on the
comparison request selects the snapshot for bare states only.

Generated drops, Resonating Core targeting, agent presets, set effects, damage
calculations, and build optimization are not part of this API.

## Errors, caching, and limits

- Errors use `application/problem+json`: `400` for malformed JSON, `409` for an
  unsupported rules version (with `supported_rules_versions`), `413` for a body
  over 32 KiB, `422` for shape, legality or replay failures, and `429` for rate
  limiting (with `Retry-After`). Validation errors have JSON Pointers in `errors`.
- Explicit rules versions and the v1 schema are cacheable with immutable ETags.
  Unversioned rules and OpenAPI revalidate; POST responses use `no-store`.
- CORS applies only to the simulation routes. Set
  `zzz.disc-simulation.allowed-origins` to a comma-separated allowlist when needed
  (default: `*`, no credentials). `zzz.disc-simulation.requests-per-minute`
  defaults to 120 requests per client per instance, using the connection's remote
  address. Behind a proxy, enforce client-specific quotas at a trusted edge or
  configure the server to obtain the client address from that trusted proxy.
- In a warmed local MockMvc check (300 requests per operation), p95 was 6.4 ms
  for a final S-rank step with 14 prior events and 9.5 ms for a 280-outcome weighted
  forecast with eight thresholds. These timings include API validation and
  serialization, not network or production load.

[Back to the REST API index](README.md#rest-api-documentation)

# abductcli — quantitative abductive reasoning engine. Clojure.

## How to Read This Project

This human cannot grasp scale.

A banana costs 2,000 won at the corner store. It's heavy. It traveled
from Southeast Asia. How many hectares of land, how many workers sweating
how many hours a day, how many cargo ships — to put bananas in every
neighborhood on Earth at this price?

The Manhattan Project built secret factory cities across the US to
produce a few kilograms of enriched uranium. How many people? How big
were the facilities? What did the logistics look like?

Anthropic's Claude is now answering "what should I eat for lunch" for
millions of non-developers on Opus. How many GPUs does that require?
How large is the datacenter? What does the power consumption look like?

The Battle of Stalingrad consumed roughly 400,000 lives (uncertain).
What scale of logistics, supply chains, and human organization makes
that possible — or inevitable?

**These are all the same question:** given a surprising fact and limited
information, can you reason backward to the hidden quantities that
must be true?

This is abduction — inference to the best explanation. Not prediction.
Not correlation. The question is:

> "I see this number. What combination of conditions would make this
> number possible? Can I cross-check from fragments of other domains?"

abductcli is a tool for practicing this. The pipeline:

```
anomaly   — a number that surprises you (any domain)
signal    — candidate explanations (possibly from a completely different domain)
memo      — a hypothesis connecting them, with evidence chain
evaluation — did it turn out right?
```

The point is not to get the right answer. The point is to build the
habit of reasoning from fragments, tracking your guesses, and learning
which cross-domain connections actually hold.

**Reference:** Geoffrey West, *Scale* — universal laws connecting
biology, cities, and companies. The same power laws appear across
domains that seem to have nothing in common.

## Quick Start

```bash
# Full pipeline (Superstore retail data — first demo dataset)
clj -M:run pipeline

# Individual steps
clj -M:run import data/superstore.csv
clj -M:run detect --grain category
clj -M:run suggest-signals --anomaly anom-001
```

## Current State — Honest Record

**Working:**
- CSV → tx JSONL (category/sub-category grain)
- Robust z-score anomaly detection
- Context registration (EDN pack bulk import)
- Signal relevance scoring (domain/entity/time/source weighted average)
- Auto-generated memo with drill-down hypothesis
- Compact JSONL export (agent-consumable surface)
- 23 tests, 79 assertions passing

**Not working yet:**
- Only one dataset (Superstore retail). Cross-domain is the goal, not single-domain.
- Time-window filtering not applied (full scan + sort only)
- Entity matching hardcoded (Furniture/Technology/Office Supplies)
- No generic import — each dataset needs its own normalizer
- Backtest is manual input only
- Pipeline wipes all data on each run (demo-first)

## Pipeline

```
1. Import     — CSV → normalize → tx JSONL (aggregated by grain)
2. Context    — register external facts (calendar, industry, macro events)
3. Detect     — robust z-score anomaly detection
3.5 Drill     — decompose anomaly entity into sub-entities
4. Signal     — rank contexts by relevance, attach to anomaly
5. Memo       — anomaly + signal + drill-down → evidence-backed hypothesis
6. Export     — compact JSONL (surface for other agents to read)
```

## Changelog

### 2026-04-16: identity pivot — from margin tool to scale reasoning
- Recognized that Superstore demo was proof-of-concept, not the destination
- Real question: cross-domain quantitative abduction (bananas, nukes, GPUs, wars)
- README rewritten around "this human cannot grasp scale"
- Pipeline structure unchanged — it generalizes beyond retail

### 2026-04-16: pipeline vertical slice
- `pipeline` command — full flow in one execution
- Signal relevance weighted average (domain/entity/time/source)
- Sub-category drill-down → improved memo hypothesis resolution
- Open questions: time-window filtering depth, entity taxonomy generalization

### 2026-04-15: anomaly→signal→memo pipeline
- 4-stage pipeline structure (anomaly→signal→memo→evaluation)
- Superstore CSV anomaly detection + context + signal + memo/backtest
- margincli → abductcli rename

### 2026-04-14: margin calculation engine + Kaggle data
- BigDecimal margin calc/reverse engine
- Kaggle Superstore CSV import

## Why This Is Hard — Cognitive Limits

Frontier models can look up "global banana production" and return a number.
That's not the problem. The problem is:

**Humans cannot hold scale in their heads.**

- I live in Suwon, South Korea. I can roughly picture Suwon in my mind.
  But Russia? How much bigger is it? Lake Baikal — is that a lake or a sea?
  I know the numbers exist, but I cannot *feel* the difference between
  400 km² and 31,722 km².

- I know the Manhattan Project employed ~125,000 people at peak.
  But I cannot picture what 125,000 people working in secret factory cities
  actually looks like. My mental model tops out at maybe a few hundred faces.

- A datacenter has 100,000 GPUs. Each GPU is the size of a book.
  100,000 books — that's a library. But a library doesn't consume 150 MW.
  The analogy breaks. My intuition doesn't transfer across dimensions
  (count → volume → power → cost).

This is not a reasoning problem. It's a **cognitive representation problem.**
Abductive reasoning helps because it forces you to:

1. Pick a specific number that surprises you
2. Ask "what would have to be true for this number to exist?"
3. Find fragments from other domains that constrain the answer
4. Write it down (memo) so you can check later

The act of writing a memo — even a wrong one — is more useful than
the feeling of "wow, that's big." The memo is falsifiable. The feeling isn't.

## Sample Questions — Scale Abduction Candidates

These are the kind of questions abductcli is for. Each one starts with
a number that doesn't fit in your head, and asks what hidden quantities
must be true.

### Q1: Banana Supply Chain
A banana costs 2,000 won (~$1.50) at my corner store. It's heavy,
it's perishable, it traveled from Southeast Asia or Latin America.
- How many hectares of land grow bananas worldwide?
- How many humans work in banana farming and logistics?
- How many refrigerated container ships are moving bananas right now?
- How can this cost only $1.50?

**LLM quick answer (unverified):** ~120M tons/year global production,
~5M hectares, 30,000-40,000 ship transits/year.
**Confidence:** Low. These are pattern-matched numbers, no source.
**Verification data:** FAO FAOSTAT (production), UN Comtrade (trade),
shipping industry reports.

### Q2: Manhattan Project Scale
The US built secret factory cities to produce a few kg of enriched uranium.
- How many people were involved? (Peak? Total over project life?)
- How large were the facilities physically?
- What was the daily resource consumption (power, water, materials)?
- How did they keep 100,000+ people secret?

**LLM quick answer (unverified):** ~125,000 peak, possibly 400,000-600,000
total. Oak Ridge ~75,000 workers. K-25 was "the largest building in the world."
Cost ~$2B in 1945 dollars.
**Confidence:** Medium for headline numbers, low for details.
**Verification data:** DOE declassified documents, Rhodes "The Making of
the Atomic Bomb," OSTI digital archives.

### Q3: LLM Inference Infrastructure
Millions of people now ask Claude Opus what to eat for lunch.
- How many GPUs serve Anthropic's traffic?
- How large is the datacenter footprint?
- What is the power consumption?
- What does one Opus inference cost in electricity?

**LLM quick answer (unverified):** 10,000-100,000 GPUs (range too wide
to be useful). Maybe 50-200 MW. Per-inference cost unknown.
**Confidence:** Very low. I'm guessing about my own infrastructure.
**Verification data:** Semianalysis reports, ML papers on per-token compute,
public cloud pricing as proxy, Anthropic's own disclosures.

### Q4: Battle of Stalingrad Logistics
~2M total casualties over 5 months. The Soviets ferried 160,000 troops
across the Volga under fire.
- What tonnage of ammunition was consumed daily?
- How many trains supplied the Soviet side?
- What was the food/water logistics for a besieged city of rubble?
- What scale of medical infrastructure handled the wounded?

**LLM quick answer (unverified):** Daily ammunition in thousands of tons.
Specific numbers uncertain. Soviet logistics through Volga crossings
were the bottleneck.
**Confidence:** Low. Eastern Front casualty numbers are notoriously disputed.
**Verification data:** Krivosheev "Soviet Casualties and Combat Losses,"
Beevor "Stalingrad," Soviet military archives (partially declassified).

### How to Use These

Each question follows the same pattern:
1. **Anomaly:** A number that doesn't fit in your head
2. **Signal candidates:** Data fragments from verifiable sources
3. **Memo:** Your best guess + which fragments support it
4. **Evaluation:** Compare your memo against actual data when found

The goal is not to answer these questions once. It's to build a practice
of tracking guesses, checking them, and learning which cross-domain
intuitions are calibrated and which are garbage.

## Tech Stack

- **Clojure 1.12+** — deps.edn
- **BigDecimal** — margin calculations (no floats)
- **Kaggle Superstore** — first demo dataset (more to come)

## License

MIT

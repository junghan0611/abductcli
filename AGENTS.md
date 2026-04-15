# AGENTS.md — margincli

## 프로젝트 정체성

에이전트 친화적 마진 분석 CLI.
운영 데이터를 원본 손실 없이 보존하고, AI가 읽을 수 있는 JSONL 단위로 재표현한다.

이 도구의 목표는 예측이 아니다.
**현재 시점의 최선 판단 재료를 드러내는 데이터 표면**이다.

## 설계 원칙

### 데이터 먼저

- 스키마를 먼저 확정하지 않는다. 원본을 잃지 않는 것이 먼저.
- 공개 데이터셋(Kaggle 등)으로 시작한다. 누구나 복제해서 돌릴 수 있어야 한다.
- 실데이터는 나중에 같은 파이프라인에 태운다.

### CLI + 에이전트 스킬

- 웹앱이 아니다. 터미널에서 돌아가는 CLI.
- pi-skills로 등록 가능한 구조 — 에이전트가 직접 호출.
- 서브커맨드: `import`, `calc`, `reverse`, `simulate`, `export`, `query`

### 3층 아키텍처

```
Raw Layer    → 원본 CSV/Excel을 JSONB로 보존 (PostgreSQL or SQLite)
AI Layer     → JSONL 재표현 — indicator / memo / evidence 단위
Human Layer  → CLI 출력 (테이블, 요약), 추후 대시보드는 부가적
```

### 예측에 대한 태도

AI가 해야 할 일은 미래를 단정하는 것이 아니라:
- 현재 시점에 어떤 데이터가 중요한지 드러내고
- 서로 흩어진 자료를 연결하며
- 사람이 결정을 내리기 위한 최선의 현재 맥락을 제공하는 것

## 기술 스택

- **언어**: Python 3.11+
- **CLI**: Click 또는 Typer
- **DB**: SQLite (기본) / PostgreSQL (확장)
- **계산**: Decimal (float 금지)
- **데이터**: Kaggle Superstore 등 공개 데이터셋
- **출력**: 테이블 (rich), JSON, JSONL

## 도메인 엔티티

| 엔티티 | 설명 |
|--------|------|
| Product | 제품. SKU, 이름, 카테고리, 원가, 원가 이력 |
| Channel | 판매 채널. 수수료율, 쿠폰 정책, 배송비 구조 |
| Event | 행사/프로모션. 기간, 할인가, 대상 제품×채널 |
| Margin | 계산 결과. 매출, 원가, 수수료, 공헌이익, 마진율 |

## AI Layer — JSONL 단위

### indicator (지표)
```json
{"type": "indicator", "name": "margin_rate", "value": 0.23, "product": "GX1", "channel": "coupang", "date": "2026-04-15"}
```

### memo (판단 메모)
```json
{"type": "memo", "text": "GX1 쿠팡 20% 할인 시 마진율 8%로 하락, 경보 수준", "date": "2026-04-15", "source": "simulation"}
```

### evidence (근거)
```json
{"type": "evidence", "claim": "Q1 평균 마진율 15%", "data_points": 342, "period": "2026-Q1", "source": "superstore.csv"}
```

## 프로젝트 구조 (목표)

```
margincli/
├── AGENTS.md
├── README.md
├── pyproject.toml
├── src/
│   └── margincli/
│       ├── __init__.py
│       ├── cli.py              # CLI 진입점
│       ├── commands/           # 서브커맨드
│       │   ├── import_cmd.py
│       │   ├── calc.py
│       │   ├── reverse.py
│       │   ├── simulate.py
│       │   ├── export.py
│       │   └── query.py
│       ├── core/
│       │   ├── engine.py       # 마진 계산 엔진 (Decimal)
│       │   ├── models.py       # 도메인 모델
│       │   └── db.py           # DB 연결
│       ├── layers/
│       │   ├── raw.py          # Raw Layer — 원본 보존
│       │   ├── ai.py           # AI Layer — JSONL 변환
│       │   └── human.py        # Human Layer — 포맷 출력
│       └── data/
│           └── schemas/        # JSONB 스키마 정의
├── data/                       # 공개 데이터셋
│   └── superstore.csv
├── tests/
└── skill/                      # pi-skills 연동
    └── SKILL.md
```

## 필수 명령어

```bash
# 설치
pip install -e .

# 테스트
pytest tests/ -v

# 데이터 임포트
python -m margincli import --dataset data/superstore.csv

# 마진 계산
python -m margincli calc --product "X" --channel "Y" --price 100
```

## 관련 문서

- [[denote:20260410T144158][전략기획실 엑셀 데이터의 AI 이해용 JSONB 데이터레이크]]
- [[denote:20250509T135957][©캐글(kaggle) ©허깅페이스(huggingface) 데이터과학 머신러닝 커뮤니티 플랫폼]]

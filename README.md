# margincli

에이전트 친화적 마진 분석 CLI.

운영 데이터(엑셀/시트/CSV)를 원본 손실 없이 모으고,
AI가 이해 가능한 JSONB + JSONL 구조로 재구성하여,
**예측이 아니라 현재 시점의 최선 판단 재료를 드러내는 도구.**

## 철학

- 예측 모델이 아니다. **현재 시점의 최선 데이터 표면**이다.
- 웹앱이 아니다. **CLI + 에이전트 스킬**이다.
- 스키마를 먼저 확정하지 않는다. **원본을 잃지 않는 것**이 먼저다.
- 대시보드는 부가적 조회면이다. 사람은 대시보드를 보고, AI는 JSONL을 읽는다.

## 3층 아키텍처

```
1. Raw Layer    — 원본 보존 (CSV/Excel → JSONB)
2. AI Layer     — JSONL 재표현 (indicator / memo / evidence 단위)
3. Human Layer  — 조회면 (CLI 출력, 추후 대시보드)
```

## 도메인

제품(SKU)의 채널별 판매에서 공헌이익(Contribution Margin)을 계산하고,
행사(할인/프로모션) 시뮬레이션과 역산을 지원한다.

핵심 엔티티:
- **Product** — 제품, 원가, 원가 이력
- **Channel** — 판매 채널, 수수료율, 정책
- **Event** — 행사/프로모션 기간, 할인가
- **Margin** — 공헌이익 계산 결과

## 공개 데이터셋

Kaggle Superstore 등 공개 데이터셋으로 시작한다.
누구나 복제해서 바로 돌려볼 수 있다.

## 사용 예시 (목표)

```bash
# 데이터 가져오기
margincli import --dataset data/superstore.csv

# 마진 계산
margincli calc --product "GX1" --channel "coupang" --price 29900

# 역산: 목표 마진율 달성을 위한 최소 판매가
margincli reverse --product "GX1" --channel "coupang" --target-margin 0.15

# 행사 시뮬레이션
margincli simulate --product "GX1" --discount 0.2 --period "2026-04-15:2026-04-30"

# JSONL 내보내기 (에이전트용)
margincli export --format jsonl --unit indicator
margincli export --format jsonl --unit evidence
```

## 에이전트 스킬

pi-skills로 등록하면 에이전트가 직접 마진 쿼리를 수행할 수 있다.

```
"이 제품 쿠팡에서 20% 할인하면 마진 얼마 남아?"
→ margincli calc + margincli simulate
```

## License

MIT

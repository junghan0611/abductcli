# Issue #1 확장 리서치 — 첫 3개 surface 선언

이 문서는 본격 surface 파일(`docs/surfaces.md`, `data/surfaces.edn`)을
직접 만들기 전에 결정과 초안을 모아둔 **리서치 노트**다. 후속 에이전트가
이 노트를 입력으로 본격 파일을 생성한다. 노트는 본격 파일 생성 후
이력으로 남기거나 삭제한다(아래 "수명" 절 참조).

surface 형식을 한 번 박으면 후속 surface의 모양이 거기에 귀속되므로,
첫 3개는 노트 단계에서 충분히 검증한 뒤 파일로 승격한다.

원 이슈: [#1 — 질문 모양의 데이터 표면을 먼저 정의하기](https://github.com/junghan0611/abductcli/issues/1)

---

## 0. 결정 요약

- **저장 위치**: `docs/surfaces.md` (서사) + `data/surfaces.edn` (머신 리딩) **둘 다**.
  - md는 의도/관측/링크가 살아 있어야 하고, 사람이 읽고 쓰는 표면이다.
  - edn은 기존 `data/packs/*.edn` 컨벤션과 동일 구조로, 미래의
    `surface` CLI 명령(별도 이슈)이 바로 로드할 수 있다.
- **첫 3개 surface**:
  1. `concert-local-scale` — 지역-콘서트-스케일
  2. `scarce-tool-community` — 희소-도구-커뮤니티
  3. `everyday-supply-chain` — 일상재-공급망-스케일 (바나나)
- **AGENTS.md / README.md 패치 초안** 포함. 적용은 후속 에이전트.
- **CLI 변경 없음**. `surface list/show` 같은 명령은 별도 이슈로 분리.

---

## 1. surface: `concert-local-scale` — 지역-콘서트-스케일

### 관측된 이상함
수원 같은 광역시급 도시에 김장훈/임영웅류 가수의 전국투어 현수막이
주기적으로 걸린다. 그러나 화자 본인도 그런 콘서트에 가본 적이 없고,
주변에서도 갔다는 사람을 거의 본 적이 없다. 그럼에도 투어는 매년 굴러간다.

### 왜 스케일 추론과 연결되는가
"내 1차 사회망 = 0명"과 "공연장은 만석에 가깝다"는 동시에 참일 수 있다.
이건 광역 생활권 인구, 팬덤의 비대칭 분포(롱테일이 아니라 압축된 헤드),
지역 문화재단 보조, 예매 플랫폼의 마케팅 풀이 결합되어
local sample이 global density를 전혀 대표하지 않는 전형적 케이스다.
Geoffrey West의 도시 superlinear scaling과 직접 붙는다.

### 후보 신호
- 공연장 좌석 수 × 회차 → 한 도시 도달 가능 인원
- 평균 티켓 가격 × 좌석 수 → 회차당 매출 하한
- 잔여석 스냅샷의 시간 변화 → 실판매율 추정
- 팬카페/SNS 회원 수 vs 그 도시 인구
- KOPIS 공연예술통합전산망의 동일 가수 연간 동원 수

### 가능한 공개 출처
- KOPIS (kopis.or.kr) — 공연 통계, 예매 통계, 좌석 점유
- 인터파크/멜론티켓/예스24 잔여석 페이지
- 공연장 홈페이지 좌석배치도
- 통계청 KOSIS — 시군구 인구, 연령분포
- 지자체 문화재단 공연 보조 공시
- 가수 공식 팬카페/유튜브/인스타 팔로워 수

### 최소 데이터 묶음
1. 같은 가수의 최근 12개월 투어 도시·일자 (KOPIS 1쿼리)
2. 각 공연장 총 좌석 수 (공연장 홈페이지 1회 수집)
3. 1~2개 공연의 잔여석 시계열 스냅샷 (수동 5회면 충분)
4. 공연 도시들의 KOSIS 인구 (1쿼리)
5. 가수 팬카페/공식 팔로워 수 (스냅샷 1회)

이게 전부다. 스포츠 통계 같은 거 안 끌어와도 된다.

### 검증 경로
- 메모: "이 가수의 1회 공연 평균 동원 = X명, 도시 인구 대비 Y%"
- 6개월 뒤 재방문해서 신규 투어의 잔여석/매진 보도와 대조
- 팬덤 추산을 좌석 점유 추산과 cross-check
- 실패 모드 명시: 잔여석 스냅샷이 회차 직전 dump일 가능성, 초대권/단체석 비율 미반영

---

## 2. surface: `scarce-tool-community` — 희소-도구-커뮤니티

### 관측된 이상함
화자 주변에는 Emacs 사용자가 거의 없고, 디지털가든을 장기 운영하는
사람도 거의 보이지 않는다. 그러나 GitHub, ELPA/MELPA, emacs-devel,
org-mode 메일링, 개인 공개 가든을 보면 **분명히** 그 사람들은 존재하고
도구는 활발히 갱신된다.

### 왜 스케일 추론과 연결되는가
지역 밀도(local density)가 0에 가까워도 네트워크 밀도(network density)는
충분히 높을 수 있다. 도구의 생존은 사용자 수 절대값이 아니라
**기여자 분포 × 갱신 주기 × 정착률**의 함수다. 이건 Dunbar 수,
Linux 커널 contributor 분포, 위키피디아 long-tail editor와 같은
"보이지 않는 소수 다중체"의 한 사례다.

### 후보 신호
- GitHub repo의 commit/contributor 시계열
- ELPA/MELPA 패키지 릴리즈 주기 분포
- emacs-devel 메일링 월간 메시지 수
- "digital garden" / "org-roam" 검색 트렌드
- 공개된 개인 가든의 backlink 그래프
- Reddit r/emacs, HN, lobste.rs 활동 주기

### 가능한 공개 출처
- GitHub REST/GraphQL API (org=emacs-mirror, search by topic)
- MELPA stats.json
- emacs-devel 메일링 아카이브 (lists.gnu.org)
- Google Trends, Algolia HN search
- 공개 디지털가든 디렉토리

### 최소 데이터 묶음
1. MELPA 패키지 수 + 최근 1년 릴리즈 횟수 (1 fetch)
2. Emacs core repo의 분기별 contributor 수 (12개 분기, 1 GraphQL)
3. emacs-devel 월간 메시지 수 12개월 (1 archive crawl)
4. "org-mode"/"digital garden" Google Trends 5년치
5. 공개 가든 30개 표본의 마지막 갱신일

이게 전부다. 전체 GitHub은 필요 없다.

### 검증 경로
- 메모: "Emacs 활성 contributor 분포 = 헤드 N명, 롱테일 M명, 한국 사용자 추정 K명"
- 1년 뒤 동일 측정 반복 → 분포 모양 유지/이탈 확인
- 한국 사용자 추정은 한국어 블로그/유튜브 코호트로 cross-check
- 실패 모드 명시: 봇 commit, 회사 명의 commit, 단발성 typo 수정 contributor

---

## 3. surface: `everyday-supply-chain` — 일상재-공급망-스케일 (바나나)

이미 `data/packs/banana-verified.edn`이 존재하므로, 이 surface는
**기존 데이터를 surface 형식으로 재해석**하는 첫 사례다.
post-hoc surface declaration → 기존 pack과의 align test.

### 관측된 이상함
수원 동네 마트에서 바나나 한 송이가 2,000원이다. 무겁고, 부패하고,
동남아·중남미에서 왔다. 그런데 전국 모든 편의점·대형마트에 매일
들어와 있다. 이 가격에 어떻게 가능한가?

### 왜 스케일 추론과 연결되는가
"내 손에 있는 송이"와 "전 지구 공급망"의 거리. 1.5달러라는 단가는
재배 면적, 농가 수, 냉장 컨테이너 회전, 항만, 수입 통관, 유통 마진을
모두 통과한 후의 결과물이다. 이걸 역추적하는 것이 abductcli의 원형
질문이다. 이미 검증된 LLM 추정과 FAO 데이터의 14% 오차는 이 surface가
실제로 굴러간다는 증거다.

### 후보 신호
- 전 세계 생산량 (톤/년)
- 재배 면적 (헥타르)
- 수출량 / 수출국 순위
- 냉장 컨테이너(reefer) 글로벌 보유 척수
- 주요 수입국 도매 단가
- 한국 수입 단가 (관세청)
- 동남아/중남미 농가 인건비

### 가능한 공개 출처
- FAOSTAT (이미 사용 중) — 생산, 면적, 수출
- UN Comtrade — 양자 무역 흐름
- 관세청 수출입무역통계 (unipass.customs.go.kr) — 한국 단가
- 한국농수산식품유통공사(aT) KAMIS — 국내 도매가
- 산업 리포트 (worldstopexports.com 등)
- ILO — 노동 통계
- IMO/UNCTAD — 컨테이너 fleet

### 최소 데이터 묶음
1. FAO 생산량/면적/수출량 *(이미 `banana-verified.edn` 안에 있음)*
2. 관세청 한국 바나나 수입 단가 시계열 (KOSIS 1쿼리)
3. Reefer 컨테이너 회전 주기 (산업 리포트 1건)
4. Ecuador/Philippines 농가 인건비 (ILO 1테이블)
5. KAMIS 국내 도매가 vs 소매가 갭 (1쿼리)

기존 `banana-verified.edn`은 (1)을 이미 커버. 이 surface는 (2)~(5)가 추가로
필요하다는 것을 명시한다.

### 검증 경로
- 이미 1차 검증 완료: LLM 120M tons vs FAO 139M, 14% under (README 참조)
- 다음 라운드: "한국 수입 단가가 농가 출하단가의 몇 배인가" 메모
- 관세청 + KAMIS로 2~3 단계 마진 cross-check
- 실패 모드: 환율 변동, 품종(Cavendish 외) 혼입, 유기농/공정무역 프리미엄

---

## 4. EDN 초안 — `data/surfaces.edn` 후보

후속 에이전트가 그대로 넣을 수 있도록.

```clojure
;; surface = 측정 표면 선언. 데이터 이전, 질문 이후.
;; 형식: data/packs/*.edn과 어긋나지 않도록 :name + :desc + :surfaces 벡터.
{:name "abductcli-surfaces-v1"
 :desc "Question-shaped measurement surfaces. Declared 2026-05-06 from issue #1."
 :surfaces
 [{:id          "concert-local-scale"
   :title       "지역-콘서트-스케일"
   :anomaly     "광역시급 도시에 전국투어 현수막은 걸리지만 1차 사회망에서는 관객이 0명에 가깝다"
   :why-scale   "local sample이 global density를 대표하지 않음. 도시 superlinear의 압축 헤드."
   :signals     ["좌석 수×회차" "티켓 가격" "잔여석 스냅샷" "팬카페 규모"
                 "KOPIS 연간 동원" "도시 인구"]
   :sources     ["KOPIS" "인터파크/멜론티켓/예스24 잔여석"
                 "공연장 좌석배치도" "KOSIS" "지자체 문화재단" "팬카페/SNS"]
   :min-pack    ["가수 12개월 투어 도시·일자" "공연장 좌석 수"
                 "1~2회 잔여석 시계열" "공연 도시 인구" "팬카페 회원 수"]
   :verify      "메모: 1회 평균 동원 X명, 도시 인구 대비 Y%. 6개월 뒤 매진 보도와 대조."}

  {:id          "scarce-tool-community"
   :title       "희소-도구-커뮤니티"
   :anomaly     "주변엔 Emacs/디지털가든 사용자가 없는데 GitHub/MELPA/메일링은 활발하다"
   :why-scale   "지역 밀도 0 ≠ 네트워크 밀도 0. 보이지 않는 소수 다중체."
   :signals     ["GitHub commit/contributor 시계열" "MELPA 릴리즈 주기"
                 "emacs-devel 월간 트래픽" "검색 트렌드" "공개 가든 갱신일"]
   :sources     ["GitHub API" "MELPA stats.json" "lists.gnu.org"
                 "Google Trends" "공개 가든 디렉토리"]
   :min-pack    ["MELPA 패키지 수 + 1년 릴리즈" "Emacs core 분기별 contributor"
                 "emacs-devel 월간 메시지" "Trends 5년" "공개 가든 30개 last-update"]
   :verify      "메모: 활성 contributor 헤드 N, 롱테일 M, 한국 사용자 K. 1년 뒤 재측정."}

  {:id          "everyday-supply-chain"
   :title       "일상재-공급망-스케일"
   :anomaly     "동네 마트의 2,000원 바나나가 매일 전국에 깔린다"
   :why-scale   "단가는 재배·물류·통관·유통의 누적 통과 결과. 역추적이 abductcli의 원형 질문."
   :signals     ["FAO 생산/면적/수출" "한국 수입 단가" "reefer 회전"
                 "농가 인건비" "도매-소매 갭"]
   :sources     ["FAOSTAT" "UN Comtrade" "관세청 unipass" "aT KAMIS"
                 "ILO" "IMO/UNCTAD"]
   :min-pack    ["banana-verified.edn (기존)" "관세청 수입단가 시계열"
                 "reefer 회전 산업 리포트" "농가 인건비" "KAMIS 도-소매 갭"]
   :verify      "1차 완료(LLM 14% under). 다음: 한국 수입가/농가 출하가 배수 메모."}]}
```

---

## 5. AGENTS.md 패치 초안

`## Design Principles` 섹션의 "Preserve originals"는 그대로 두고,
새로운 원칙을 **앞쪽에** 추가한다.

~~~md
### Question first, surface second, dataset third
- 출발점은 공개 데이터셋 검색이 아니라, 인간이 실제로 마주친 스케일 어긋남이다.
- 데이터셋을 찾기 전에 다음 5가지를 명시한다:
  1. 인간이 실제로 이상하게 느낀 장면은 무엇인가?
  2. 어떤 스케일 간격을 보고 있는가?
  3. 그 간격을 드러낼 수 있는 측정 표면(surface)은 무엇인가?
  4. 그 표면을 지탱할 최소 공개 데이터는 무엇인가?
  5. 나중에 이 메모를 반증하거나 보강할 수 있는가?
- 정의된 surface는 `data/surfaces.edn`에 등록하고, 서사는 `docs/surfaces.md`에 둔다.
- "깨끗한 공개 데이터셋부터 찾는다"는 안티패턴이다. NBA·농수산·임의 Kaggle 표는
  surface가 그것을 요구할 때만 가져온다.
~~~

그리고 `## 3층 아키텍처` 섹션 위에 한 줄 추가:

~~~md
파이프라인 앞단: human question → declared surface → signal candidates →
minimal data pack → memo → verification.
~~~

---

## 6. README.md 패치 초안

`## Sample Questions — Scale Abduction Candidates` 섹션 도입부 한 단락을
다음으로 교체.

~~~md
## Sample Questions — Surfaces, not datasets

abductcli는 데이터셋이 아니라 **측정 표면(surface)**으로 시작한다.
surface = 인간이 실제로 마주친 스케일 어긋남을 측정 가능한 형태로 선언한 것.
아래는 첫 3개 표면이다. 각 표면은 `data/surfaces.edn`에 머신 리딩
가능한 형태로, `docs/surfaces.md`에 서사로 기록된다.

- `concert-local-scale` — 1차 사회망 0명 vs 매년 굴러가는 전국투어
- `scarce-tool-community` — 주변엔 없는데 GitHub엔 살아 있는 도구·실천
- `everyday-supply-chain` — 동네 마트 2,000원 바나나의 누적 통과 단가
~~~

기존 Q1~Q4(바나나/맨해튼/LLM 인프라/스탈린그라드)는 surface 카탈로그
하위 항목으로 재배치(추후 surface로 정식 등록).

---

## 7. 후속 에이전트 작업 순서

1. `docs/surfaces.md` 생성 — 위 섹션 1~3을 그대로 옮기되, 6필드 헤더를
   유지하고 마크다운 링크는 라이브로 검증.
2. `data/surfaces.edn` 생성 — 위 섹션 4의 EDN을 그대로 사용.
3. `AGENTS.md` 패치 — 위 섹션 5 적용. 기존 "Preserve originals" 원칙은 보존.
4. `README.md` 패치 — 위 섹션 6 적용. 기존 Q1~Q4는 삭제하지 말고
   "Surface 후보 (미선언)" 하위로 이동.
5. surface→pack 정합성 자기 검증 — `everyday-supply-chain.min-pack[0]`이
   실제 `banana-verified.edn`의 어떤 `:ctx-type` 레코드와 매핑되는지 1줄 코멘트.
6. CLI 변경은 이번 PR에 넣지 않는다. 별도 이슈로 `surface list/show` 명령을 발굴.

---

## 8. 이 노트의 수명

- 본격 파일 생성 후, 후속 에이전트는 이 노트를 다음 중 하나로 처리:
  - **(권장)** 이 경로(`docs/research/issue-1-surfaces.md`)에 그대로 두되,
    상단에 "→ 적용 완료, ref: <commit-sha>" 한 줄 추가.
  - **(대안)** 삭제 — 단, 적용 PR 본문에 이 노트 전문을 인용해 둔 경우에 한함.

(ns margincli.core
  "margincli — 에이전트 친화적 마진 분석 CLI.
   데이터는 코드다."
  (:gen-class))

(defn -main [& args]
  (println "margincli — margin analysis CLI for agents")
  (println "Usage: margincli <command> [options]")
  (println)
  (println "Commands:")
  (println "  import    CSV/Excel 데이터 가져오기")
  (println "  calc      마진 계산")
  (println "  reverse   목표 마진율 → 최소 판매가 역산")
  (println "  simulate  행사 시뮬레이션")
  (println "  export    JSONL 내보내기 (에이전트용)")
  (println "  query     데이터 조회"))

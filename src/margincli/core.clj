(ns margincli.core
  "margincli — 에이전트 친화적 마진 분석 CLI.
   데이터는 코드다."
  (:require [margincli.import :as imp]
            [margincli.engine :as eng]
            [clojure.string :as str])
  (:gen-class))

;; ── 커맨드: import ────────────────────────────────

(defn cmd-import
  "CSV 파일 임포트 + 요약 출력."
  [args]
  (let [path (first args)]
    (if (nil? path)
      (println "Usage: margincli import <csv-path>")
      (if-not (.exists (java.io.File. path))
        (println (str "파일 없음: " path))
        (let [result (imp/import-csv path)]
          (imp/print-summary result))))))

;; ── 커맨드: calc ──────────────────────────────────

(defn- parse-opts
  "CLI 옵션 파싱. --key value 쌍 → {:key value} 맵."
  [args]
  (into {}
    (map (fn [[k v]] [(keyword (str/replace k #"^--" "")) v])
         (partition 2 args))))

(defn cmd-calc
  "마진 계산.
   --price, --cost, --commission 파라미터."
  [args]
  (let [opts       (parse-opts args)
        price      (some-> (:price opts) bigdec)
        cost       (some-> (:cost opts) bigdec)
        commission (some-> (:commission opts) bigdec)]
    (if (or (nil? price) (nil? cost))
      (do (println "Usage: margincli calc --price <판매가> --cost <원가> [--commission <수수료율>]")
          (println "  예: margincli calc --price 29900 --cost 15000 --commission 0.12"))
      (let [comm-rate (or commission 0M)
            result    (eng/calc-margin price cost comm-rate)]
        (println "margincli — 마진 계산")
        (println)
        (println (str "  판매가:   " (:sale-price result)))
        (println (str "  원가:     " (:cost result)))
        (println (str "  수수료:   " (:commission result)
                      " (" (.multiply comm-rate 100M) "%)"))
        (println (str "  공헌이익: " (:contribution result)))
        (println (str "  마진율:   " (.multiply (:margin-rate result) 100M) "%"))))))

;; ── 커맨드: reverse ───────────────────────────────

(defn cmd-reverse
  "목표 마진율 → 최소 판매가 역산."
  [args]
  (let [opts       (parse-opts args)
        target     (some-> (:target opts) bigdec)
        cost       (some-> (:cost opts) bigdec)
        commission (some-> (:commission opts) bigdec)]
    (if (or (nil? target) (nil? cost))
      (do (println "Usage: margincli reverse --target <목표마진율> --cost <원가> [--commission <수수료율>]")
          (println "  예: margincli reverse --target 0.25 --cost 15000 --commission 0.12"))
      (let [comm-rate (or commission 0M)
            result    (eng/reverse-margin target cost comm-rate)]
        (if (:error result)
          (println (str "오류: " (:error result)))
          (do
            (println "margincli — 역산")
            (println)
            (println (str "  목표 마진율: " (.multiply target 100M) "%"))
            (println (str "  원가:       " cost))
            (println (str "  수수료율:   " (.multiply comm-rate 100M) "%"))
            (println (str "  → 최소 판매가: " (:min-sale-price result)))
            (println)
            (println "  검증:")
            (let [v (:verify result)]
              (println (str "    공헌이익: " (:contribution v)))
              (println (str "    마진율:   " (.multiply (:margin-rate v) 100M) "%")))))))))

;; ── 메인 ──────────────────────────────────────────

(defn -main [& args]
  (let [cmd       (first args)
        rest-args (rest args)]
    (case cmd
      "import"  (cmd-import rest-args)
      "calc"    (cmd-calc rest-args)
      "reverse" (cmd-reverse rest-args)
      ;; 도움말
      (do
        (println "margincli — 에이전트 친화적 마진 분석 CLI")
        (println)
        (println "Commands:")
        (println "  import <csv>                                 CSV 데이터 가져오기")
        (println "  calc --price P --cost C [--commission R]     마진 계산")
        (println "  reverse --target T --cost C [--commission R] 목표마진→최소판매가")
        (println)
        (println "Examples:")
        (println "  margincli import data/superstore.csv")
        (println "  margincli calc --price 29900 --cost 15000 --commission 0.12")
        (println "  margincli reverse --target 0.25 --cost 15000 --commission 0.12")))))

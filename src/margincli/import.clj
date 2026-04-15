(ns margincli.import
  "Raw Layer — CSV → Clojure 맵 → 정규화.
   원본 보존 우선. 스키마를 먼저 확정하지 않는다."
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.string :as str]))

;; ── 파싱 헬퍼 ─────────────────────────────────────

(defn parse-decimal
  "문자열 → BigDecimal. 빈 문자열이나 nil은 0M."
  [s]
  (if (or (nil? s) (str/blank? s))
    0M
    (bigdec (str/trim s))))

(defn parse-int
  "문자열 → int. 빈 문자열이나 nil은 0."
  [s]
  (if (or (nil? s) (str/blank? s))
    0
    (Integer/parseInt (str/trim s))))

;; ── CSV 파싱 ──────────────────────────────────────

(defn csv->maps
  "CSV 파일 → 맵 시퀀스. 첫 행을 헤더로 사용."
  [path]
  (with-open [reader (io/reader path)]
    (let [rows (csv/read-csv reader)
          headers (mapv str/trim (first rows))
          data (rest rows)]
      (doall
       (map (fn [row]
              (zipmap headers (mapv str/trim row)))
            data)))))

;; ── 도메인 매핑 ───────────────────────────────────

(defn normalize-row
  "Superstore 원본 행 → margincli 도메인 맵.
   원본 필드를 :raw에 보존하면서 도메인 키를 추가."
  [row]
  (let [sales    (parse-decimal (get row "Sales"))
        profit   (parse-decimal (get row "Profit"))
        quantity (parse-int (get row "Quantity"))
        discount (parse-decimal (get row "Discount"))
        cost     (- sales profit)
        unit-price (if (pos? quantity)
                     (.divide sales (bigdec quantity) 2 java.math.RoundingMode/HALF_UP)
                     0M)
        unit-cost  (if (pos? quantity)
                     (.divide cost (bigdec quantity) 2 java.math.RoundingMode/HALF_UP)
                     0M)
        margin-rate (if (and (pos? sales) (not (zero? sales)))
                      (.divide profit sales 4 java.math.RoundingMode/HALF_UP)
                      0M)]
    {:raw row
     :product {:id           (get row "Product ID")
               :name         (get row "Product Name")
               :category     (get row "Category")
               :sub-category (get row "Sub-Category")}
     :channel {:region  (get row "Region")
               :segment (get row "Segment")}
     :order   {:id        (get row "Order ID")
               :date      (get row "Order Date")
               :ship-date (get row "Ship Date")
               :ship-mode (get row "Ship Mode")}
     :margin  {:sales       sales
               :profit      profit
               :cost        cost
               :quantity    quantity
               :discount    discount
               :unit-price  unit-price
               :unit-cost   unit-cost
               :margin-rate margin-rate}}))

;; ── 임포트 ────────────────────────────────────────

(defn import-csv
  "CSV 파일 임포트 → 정규화된 맵 벡터 + 요약."
  [path]
  (let [raw-rows (csv->maps path)
        normalized (mapv normalize-row raw-rows)
        total-sales (reduce + 0M (map #(get-in % [:margin :sales]) normalized))
        total-profit (reduce + 0M (map #(get-in % [:margin :profit]) normalized))
        categories (distinct (map #(get-in % [:product :category]) normalized))
        regions (distinct (map #(get-in % [:channel :region]) normalized))
        segments (distinct (map #(get-in % [:channel :segment]) normalized))
        by-category (group-by #(get-in % [:product :category]) normalized)]
    {:rows normalized
     :summary {:total-rows    (count normalized)
               :total-sales   total-sales
               :total-profit  total-profit
               :margin-rate   (if (pos? total-sales)
                                (.divide total-profit total-sales 4 java.math.RoundingMode/HALF_UP)
                                0M)
               :categories    (vec categories)
               :regions       (vec regions)
               :segments      (vec segments)
               :by-category   (into {}
                                (map (fn [[cat rows]]
                                       (let [cat-sales (reduce + 0M (map #(get-in % [:margin :sales]) rows))
                                             cat-profit (reduce + 0M (map #(get-in % [:margin :profit]) rows))]
                                         [cat {:rows   (count rows)
                                               :sales  cat-sales
                                               :profit cat-profit
                                               :margin-rate (if (pos? cat-sales)
                                                              (.divide cat-profit cat-sales 4 java.math.RoundingMode/HALF_UP)
                                                              0M)}]))
                                     by-category))}}))

;; ── 출력 ──────────────────────────────────────────

(defn print-summary
  "임포트 요약 출력."
  [{:keys [summary]}]
  (println "margincli — import 완료")
  (println)
  (println (str "  총 행:     " (:total-rows summary)))
  (println (str "  총 매출:   " (:total-sales summary)))
  (println (str "  총 이익:   " (:total-profit summary)))
  (println (str "  마진율:    " (.multiply (:margin-rate summary) 100M) "%"))
  (println (str "  카테고리:  " (str/join ", " (:categories summary))))
  (println (str "  지역:      " (str/join ", " (:regions summary))))
  (println (str "  고객유형:  " (str/join ", " (:segments summary))))
  (println)
  (println "  카테고리별:")
  (doseq [[cat info] (sort-by key (:by-category summary))]
    (println (str "    " cat
                  "  행:" (:rows info)
                  "  매출:" (:sales info)
                  "  이익:" (:profit info)
                  "  마진:" (.multiply (:margin-rate info) 100M) "%"))))

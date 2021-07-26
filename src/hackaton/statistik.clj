(ns hackaton.statistik
  (:require [hackaton.timer :as timer])
  )

"Namespace til at håndterer statistik håndteringen. "

(defn average [coll]
  (if (= 0 (count coll))
    0
    (/ (reduce + coll) (count coll))))

(defn træ-alder [træ]
  (- (get træ :sluttick 0)
     (get træ :starttick 0))
  )

(defn beregn-gennemsnit
  ([kollektion]
   (int (average (map træ-alder kollektion))))
  ([kollektion antal]
   (int (average (map træ-alder (take antal kollektion))))))


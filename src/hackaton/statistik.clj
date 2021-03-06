(ns hackaton.statistik
  (:require [hackaton.timer :as timer])
  )

"Namespace til at håndterer statistik håndteringen. "

(defn average [coll]
  (if (= 0 (count coll))
    0
    (/ (reduce + coll) (count coll))))

(defn træ-alder [træ]
  (- (:sluttick træ) (:starttick træ))
  )

(defn beregn-gennemsnit
  ([kollektion]
   (average (map træ-alder kollektion)))
  ([kollektion antal]
   (average (map træ-alder (take antal kollektion)))))


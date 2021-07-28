(ns hackaton.statistik
  (:require [hackaton.timer :as timer])
  )

"Namespace til at håndterer statistik håndteringen. "

(defn gennemsnit
  "Finder gennemsnittet"
  [coll]
  (if (empty? coll)
    0
    (/ (reduce + coll) (count coll))))

(defn median
  "Finder medianen"
  [kollektion]
  (if (empty? kollektion)
    0
    (let [antal (count kollektion)
          halvdelen (int (/ antal 2))]
      (nth (sort kollektion) halvdelen)
      )))


(defn træ-alder
  "Finder alderen på træet. Intet fancy"
  [træ]
  (- (get træ :sluttick 0)
     (get træ :starttick 0))
  )

(defn beregn-gennemsnit
  "Beregner gennemsnits alderen for alle træer i køen. Eventuelt kun for de seneste antal elementer"
  ([køen]
   (int (gennemsnit (map træ-alder køen))))
  ([køen antal]
   (int (gennemsnit (map træ-alder (take-last antal køen))))))

(defn beregn-median
  "Beregner median alderen for alle træer i køen. Eventuelt kun for de seneste antal elementer"
  ([køen]
   (int (median (map træ-alder køen))))
  ([køen antal]
   (int (median (map træ-alder (take-last antal køen))))))

(ns hackaton.core
  (:require [hackaton.skov :as skov]
            [shams.priority-queue :as pq]
            [hackaton.daemning :as daemning]
            ))

(def system (atom {}))

(defn init-system
  [_]
  {
   :ventetid-mellem-ticks-i-millisekunder 1000
   :dæmninger                             []
   :andre-ting                            :som-jeg-har-glemt
   })

(defn start-system
  []
  "Det er denne funktion, som skal startes for at alt kører."
  (swap! system init-system)
  (daemning/example)
  )







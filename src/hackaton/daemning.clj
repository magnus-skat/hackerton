(ns hackaton.daemning
  (:require [shams.priority-queue :as pq]
            [hackaton.skov :as skov]
            [hackaton.timer :as timer]
            ))
"https://www.tutorialspoint.com/clojure/clojure_watchers.htm"

(defn a-queue
  ([] ((pq/priority-queue #(:error %))))
  ([coll]
   (let [kø (pq/priority-queue #(:error %))]
     reduce conj kø coll)))

(def min-kø (a-queue []))

(defn ticker-har-ticket
  [key atom old-state new-state]
  (println "Uret har ticket")
  (println "Før værdi" old-state)
  (println "Ny værdi" new-state)
  )


(defn example []
  (let [
        min-kø (a-queue)
        ]
    (add-watch timer/tick :watcher
               ticker-har-ticket)))

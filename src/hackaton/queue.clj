(ns hackaton.queue
  (:require [shams.priority-queue :as pq]
            [hackaton.skov :as skov])
  (:import (clojure.lang PersistentQueue)))

"https://www.tutorialspoint.com/clojure/clojure_watchers.htm "
"https://cljdoc.org/d/shams/priority-queue/0.1.2/doc/readme"

"Dette er en priority-queue som sorterer på de elementer som har en høj error værdi"

"Man kan indsætte i køen med conj som

(def min-kø (conj min-kø (værdi)))

pop vil returnerer den nye kø, så hvis man skal have data ud, skal man huske at peek først.

(peek min-kø)

(def min-kø (pop min-kø))
"

(defn queue
  ([] ((pq/priority-queue #(:error %))))
  ([coll]
   (let [kø (pq/priority-queue #(:error %)) ]
     reduce conj kø coll)))

(defn a-queue
  ([] (PersistentQueue/EMPTY))
  ([coll]
   (reduce conj PersistentQueue/EMPTY coll)))

#_(def min-kø (atom (a-queue [(skov/fæld-træ 1 0) (skov/fæld-træ 1 0) (skov/fæld-træ 1 0) (skov/fæld-træ 1 0) (skov/fæld-træ 1 0)])))


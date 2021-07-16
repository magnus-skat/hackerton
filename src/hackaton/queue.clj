(ns hackaton.queue
  (:require [shams.priority-queue :as pq]
            [hackaton.skov :as skov]))

(defn a-queue
  ([] ((pq/priority-queue #(:error %))))
  ([coll]
   (let [kø (pq/priority-queue #(:error %)) ]
     reduce conj kø coll)))

(defn pop-kø!
  [kø]
  (let [peeked (peek kø)]
    (pop kø)
    peeked
    )
  )

(def min-kø (atom (a-queue [(skov/fæld-træ 1) (skov/fæld-træ 1) (skov/fæld-træ 1) (skov/fæld-træ 1) (skov/fæld-træ 1)])))


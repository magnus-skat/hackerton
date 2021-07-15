(ns hackaton.damm
  (:require [shams.priority-queue :as pq]
            [hackaton.skov :as skov]
            ))

(defn a-queue
  ([] ((pq/priority-queue #(:error %))))
  ([coll]
   (let [kø (pq/priority-queue #(:error %)) ]
     reduce conj kø coll)))

(def min-kø (a-queue [(skov/fæld-træ 1) (skov/fæld-træ 1) (skov/fæld-træ 1) (skov/fæld-træ 1) (skov/fæld-træ 1) ]))



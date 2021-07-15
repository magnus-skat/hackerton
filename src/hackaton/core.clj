(ns hackaton.core
  (:require [hackaton.skov :as skov]
            [shams.priority-queue :as pq]
            ))


(def system (atom {
                    :ticks 0
                    :ventetid-mellem-ticks-i-sekunder 1
                    :dæmninger []
                    :andre-ting :som-jeg-har-glemt
                    }))

(println (skov/fæld-træ :empty))





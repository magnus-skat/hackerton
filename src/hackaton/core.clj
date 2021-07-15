(ns hackaton.core
  (:require [hackaton.skov :as skov]))


(def @system (atom {
                    :ticks 0
                    :dæmninger []
                    :andre-ting :som-jeg-har-glemt
                    }))


(println (skov/fæld-træ :empty))



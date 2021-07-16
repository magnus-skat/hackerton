(ns hackaton.core
  (:require [hackaton.skov :as skov]
            [shams.priority-queue :as pq]
            [hackaton.daemning :as daemning]
            [hackaton.timer :as timer]
            ))

"For at kører dette på dit eget system, så skal du starte en REPL og så kører
start-system. Der bliver så oprettet en dæmning, som hvert sekund vil skrive noget ud
på skærmen
Det er muligt at ændre tiden imellem ticks i realtime ved at køre
(reset! hackaton.timer/ventetid 2000)
i din REPL

"

(def system (atom {}))

(defn init-system
  [_]
  {
   :ventetid-mellem-ticks-i-millisekunder 1000 ;; Bliver ikke brugt lige nu
   :dæmninger                             []
   :andre-ting                            :som-jeg-har-glemt
   })

(defn start-system
  []
  "Det er denne funktion, som skal startes for at alt kører."
  (swap! system init-system)
  (.start (Thread. timer/start-timer ))
  (daemning/example)
  )




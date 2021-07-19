(ns hackaton.core
  (:require [hackaton.queue :as queue]
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
  (let [
        ;; For det første skal vi bruge et par køer.
        fejl-kø (atom (queue/a-queue []));; Den foreste kø, som alle dæmninger smider en træstamme i, hvis der er en fejl
        første-kø (atom (queue/a-queue [])) ;; Kø'en der hører til den første dæmning.
        slut-liste (atom (queue/a-queue [])) ;; Der hvor alt ender når tiden er gået.
        ;; Er der flere køer skal de navngives og puttes på denne liste.
       ]
  (swap! system init-system)

  (.start (Thread. timer/start-timer ))

  (daemning/skovarbejder 12 "Skovarbejder" første-kø fejl-kø))

  )




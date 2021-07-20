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

;; For det første skal vi bruge et par køer.
(def første-kø (atom (queue/a-queue []))) ;; Kø'en der hører til den første dæmning.
(def anden-kø (atom (queue/a-queue []))) ;; Kø'en der hører til den anden dæmning.
(def tredie-kø (atom (queue/a-queue []))) ;; Kø'en der hører til den tredje dæmning.
(def fjerde-kø (atom (queue/a-queue []))) ;; Kø'en der hører til den fjerde dæmning.
(def femte-kø (atom (queue/a-queue []))) ;; Kø'en der hører til den femte dæmning.
(def fejl-kø (atom (queue/a-queue []))) ;; Den foreste kø, som alle dæmninger smider en træstamme i, hvis der er en fejl
(def slut-liste (atom (queue/a-queue []))) ;; Den foreste kø, som alle dæmninger smider en træstamme i, hvis der er en fejl
;; Er der flere køer skal de navngives og puttes på denne liste.

(defn init-system
  [_]
  {
   :ventetid-mellem-ticks-i-millisekunder 1000 ;; Bliver ikke brugt lige nu
   :dæmninger                             []
   :andre-ting                            :som-jeg-har-glemt
   })

(defn log-udput
  [key atom old-state new-state]
  (let [output {:tick @timer/tick
                :fejlkø (count @fejl-kø)
                :førstekø (count @første-kø)
                :andenkø (count @anden-kø)
                :trediekø (count @tredie-kø)
                :fjerde (count @fjerde-kø)
                :femte (count @femte-kø)
                :slutliste (count @slut-liste)}]
  (println "*******************")
  (println output)
  (println "*******************")
  ))

(defn start-system
  []
  "Det er denne funktion, som skal startes for at alt kører."

  (swap! system init-system)

  (.start (Thread. timer/start-timer))
  (add-watch timer/tick :log log-udput)
  (daemning/skovarbejder 0 "Skovarbejder" første-kø fejl-kø, nil)
  (daemning/dæmning 4 "Dæmning 1" anden-kø første-kø, fejl-kø)
  (daemning/dæmning 2 "Dæmning 2" tredie-kø anden-kø, fejl-kø)
  (daemning/dæmning 10 "Dæmning 3" fjerde-kø tredie-kø, fejl-kø)
  (daemning/dæmning 2 "Dæmning 4" femte-kø fjerde-kø, fejl-kø)
  (daemning/dæmning 12 "Dæmning 5" slut-liste femte-kø, fejl-kø)
)

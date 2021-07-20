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
(def slut-liste (atom (queue/a-queue []))) ;; Den sidste kø, som alle træstammer havner i
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
  (daemning/skovarbejder 0 "Skovarbejder" fejl-kø første-kø nil false)
  (daemning/dæmning 1 "Dæmning 1" første-kø anden-kø fejl-kø false)
  (daemning/dæmning 2 "Dæmning 2" anden-kø tredie-kø fejl-kø false)
  (daemning/dæmning 3 "Dæmning 3" tredie-kø fjerde-kø fejl-kø false)
  (daemning/dæmning 4 "Dæmning 4" fjerde-kø femte-kø fejl-kø false)
  (daemning/dæmning 5 "Dæmning 5" femte-kø slut-liste fejl-kø true)
)

(defn slut-log []
  (map #(- (:sluttick %) (:starttick %)) @slut-liste)
  )

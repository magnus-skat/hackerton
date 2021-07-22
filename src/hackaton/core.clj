(ns hackaton.core
  (:require [hackaton.queue :as queue]
            [hackaton.daemning :as daemning]
            [hackaton.timer :as timer]
            [hackaton.statistik :as statistik]
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
(def første-kø (atom (queue/a-queue [])))                   ;; Kø'en der hører til den første dæmning.
(def anden-kø (atom (queue/a-queue [])))                    ;; Kø'en der hører til den anden dæmning.
(def tredie-kø (atom (queue/a-queue [])))                   ;; Kø'en der hører til den tredje dæmning.
(def fjerde-kø (atom (queue/a-queue [])))                   ;; Kø'en der hører til den fjerde dæmning.
(def femte-kø (atom (queue/a-queue [])))                    ;; Kø'en der hører til den femte dæmning.
(def fejl-kø (atom (queue/a-queue [])))                     ;; Den foreste kø, som alle dæmninger smider en træstamme i, hvis der er en fejl
(def slut-liste (atom (vector)))                            ;; Den sidste kø, som alle træstammer havner i
(def fejl-liste (atom (vector)))                            ;; En liste hvor alle fejlet stammer bliver noteret
;; Er der flere køer skal de navngives og puttes på denne liste.

(defn init-system
  [_]
  {
   :køer       [{:navn "første-kø" :kø første-kø} {:navn "anden-kø" :kø anden-kø} {:kø tredie-kø :navn "tredie-kø"} {:navn "fjerde-kø" :kø fjerde-kø} {:navn "femte-kø" :kø femte-kø} {:navn "fejl-kø" :kø fejl-kø} {:navn "slut-liste" :kø slut-liste}]
   :dæmninger  [{
                 :navn       "Dæmning1"
                 :ventetid   1
                 :ind-kø     første-kø
                 :ud-kø      anden-kø
                 :fejl-kø    fejl-kø
                 :fejl-liste fejl-liste
                 :sidste?     false
                 :fejl-procent 5
                 :kø-størrelse 12
                 }
                {
                 :navn       "Dæmning2"
                 :ventetid   3
                 :ind-kø     anden-kø
                 :ud-kø      tredie-kø
                 :fejl-kø    fejl-kø
                 :fejl-liste fejl-liste
                 :fejl-procent 5
                 :kø-størrelse 12
                 :sidste?     false
                 }
                {
                 :navn       "Dæmning3"
                 :ventetid   2
                 :ind-kø     tredie-kø
                 :ud-kø      fjerde-kø
                 :fejl-kø    fejl-kø
                 :fejl-liste fejl-liste
                 :fejl-procent 5
                 :kø-størrelse 12
                 :sidste?     false
                 }
                {
                 :navn       "Dæmning4"
                 :ventetid   5
                 :ind-kø     fjerde-kø
                 :ud-kø      femte-kø
                 :fejl-kø    fejl-kø
                 :fejl-liste fejl-liste
                 :fejl-procent 5
                 :kø-størrelse 12
                 :sidste?     false
                 }
                {
                 :navn       "Dæmning5"
                 :ventetid   2
                 :ind-kø     femte-kø
                 :ud-kø      slut-liste
                 :fejl-kø    fejl-kø
                 :fejl-liste fejl-liste
                 :fejl-procent 5
                 :kø-størrelse 12
                 :sidste?     true
                 }]

   :andre-ting :som-jeg-har-glemt
   })

(defn create-logging-solution [køer ticker slut-liste]
  {:tick @ticker
   :avg  (int (statistik/beregn-gennemsnit @slut-liste 10))
   :køer {}
   :k2   {
          :fejl-kø    {:antal (count @fejl-kø)}
          :første-kø  {:antal (count @første-kø)}
          :anden-kø   {
                       :antal (count @anden-kø)
                       :avg   (statistik/beregn-gennemsnit @anden-kø)
                       }
          :tredie-kø  {:antal (count @tredie-kø)
                       :avg   (statistik/beregn-gennemsnit @tredie-kø)}
          :fjerde-kø  {:antal (count @fjerde-kø)
                       :avg   (statistik/beregn-gennemsnit @fjerde-kø)}
          :femte-kø   {:antal (count @femte-kø)
                       :avg   (statistik/beregn-gennemsnit @femte-kø)}
          :slut-liste {:antal (count @slut-liste)}}
   }
  )

(defn log-udput
  [key atom old-state new-state]
  (let [output {:tick @timer/tick
                :avg  (int (statistik/beregn-gennemsnit @slut-liste 10))
                :køer {
                       :fejl-kø    {:antal (count @fejl-kø)}
                       :første-kø  {:antal (count @første-kø)}
                       :anden-kø   {
                                    :antal (count @anden-kø)
                                    :avg   (statistik/beregn-gennemsnit @anden-kø)
                                    }
                       :tredie-kø  {:antal (count @tredie-kø)
                                    :avg   (statistik/beregn-gennemsnit @tredie-kø)}
                       :fjerde-kø  {:antal (count @fjerde-kø)
                                    :avg   (statistik/beregn-gennemsnit @fjerde-kø)}
                       :femte-kø   {:antal (count @femte-kø)
                                    :avg   (statistik/beregn-gennemsnit @femte-kø)}
                       :slut-liste {:antal (count @slut-liste)}}
                }
        ]
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
  (map daemning/byg-dæmning! (:dæmninger @system))
  )

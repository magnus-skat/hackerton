1 (ns hackaton.core
    (:require [hackaton.queue :as queue]
              [hackaton.daemning :as daemning]
              [hackaton.timer :as timer]
              [hackaton.statistik :as statistik]
              [hackaton.skov :as skov]))

"For at kører dette på dit eget system, så skal du starte en REPL og så kører
start-system. Der bliver så oprettet en dæmning, som hvert sekund vil skrive noget ud
på skærmen
Det er muligt at ændre tiden imellem ticks i realtime ved at køre
(reset! hackaton.timer/ventetid 2000)
i din REPL
"

(def system (atom {}))

;; For det første skal vi bruge et par køer.
(def første-kø (atom (queue/prioritets-kø [])))             ;; Kø'en der hører til den første dæmning.
(def anden-kø (atom (queue/prioritets-kø [])))              ;; Kø'en der hører til den anden dæmning.
(def tredie-kø (atom (queue/prioritets-kø [])))             ;; Kø'en der hører til den tredje dæmning.
(def fjerde-kø (atom (queue/prioritets-kø [])))             ;; Kø'en der hører til den fjerde dæmning.
(def femte-kø (atom (queue/prioritets-kø [])))              ;; Kø'en der hører til den femte dæmning.
(def fejl-kø (atom (queue/prioritets-kø [])))               ;; Den foreste kø, som alle dæmninger smider en træstamme i, hvis der er en fejl

(def slut-liste (atom (vector)))                            ;; Den sidste kø, som alle træstammer havner i
(def fejl-liste (atom (vector)))                            ;; En liste hvor alle fejlet træer bliver noteret.

;; Er der flere køer skal de navngives og puttes på denne liste.

(defn init-system
  [_]
  {
   :dæmninger       [{
                      :navn         "Dæmning1"
                      :ventetid     1
                      :ind-kø       første-kø
                      :ud-kø        anden-kø
                      :fejl-kø      fejl-kø
                      :fejl-liste   fejl-liste
                      :fejl-procent 5
                      :kø-størrelse 12
                      :sidste?      false
                      }
                     {
                      :navn         "Dæmning2"
                      :ventetid     3
                      :ind-kø       anden-kø
                      :ud-kø        tredie-kø
                      :fejl-kø      første-kø
                      :fejl-liste   fejl-liste
                      :fejl-procent 5
                      :kø-størrelse 12
                      :sidste?      false
                      }
                     {
                      :navn         "Dæmning3"
                      :ventetid     2
                      :ind-kø       tredie-kø
                      :ud-kø        fjerde-kø
                      :fejl-kø      første-kø
                      :fejl-liste   fejl-liste
                      :fejl-procent 5
                      :kø-størrelse 12
                      :sidste?      false
                      }
                     {
                      :navn         "Dæmning4"
                      :ventetid     5
                      :ind-kø       fjerde-kø
                      :ud-kø        femte-kø
                      :fejl-kø      anden-kø
                      :fejl-liste   fejl-liste
                      :fejl-procent 5
                      :kø-størrelse 12
                      :sidste?      false
                      }
                     {
                      :navn         "Dæmning5"
                      :ventetid     2
                      :ind-kø       femte-kø
                      :ud-kø        slut-liste
                      :fejl-kø      anden-kø
                      :fejl-liste   fejl-liste
                      :fejl-procent 5
                      :kø-størrelse 12
                      :sidste?      true
                      }
                     ]
   :antal-dæmninger 5
   :andre-ting      :som-jeg-har-glemt
   })

(defn tilføj-kø
  [navn]
  (let [
        kø (atom (queue/prioritets-kø []))
        ]
    (swap! system update :køer conj {:navn navn :kø kø})
    kø
    )
  )

(defn create-logging-solution [køer ticker slut-liste]
  {:tick @ticker
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
          :slut-liste {:antal (count @slut-liste)}}
   }
  )

(defn log-udput
  [key atom old-state new-state]
  "agents skal have fire argumenter.
   key er navnet som den fik ved oprettelsen
   atom, atomeet som er ændret
   old-state, den gamle værdi
   new-state den nye værdi"

  (let [
        output {:tick new-state
                :avg  (int (statistik/beregn-gennemsnit @slut-liste 10))
                :median  (int (statistik/beregn-gennemsnit @slut-liste 10))
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
  (.start (Thread. timer/start-timer)) ;; kan kaldes med eller uden en værdi. Hvis uden er standard 250 ticks

  (add-watch timer/tick :log log-udput)

  (daemning/skovarbejder 0 "Skovarbejder" fejl-kø første-kø nil false)
  (map daemning/byg-dæmning! (:dæmninger @system))
  )

(defn tilføj-dæmning!
  "Tilføjer en dæmning til systemet. Vil blive tilføjet til sidst i rækken"
  [ny-dæmning]
  (timer/stop)                                              ;; stop uret, så der ikke kommer konflikter
  (Thread/sleep @timer/ventetid)                            ;; vent på at alle tråde er færdige
  (let
    [
     ny-kø (tilføj-kø (:navn ny-dæmning))
     ny-dæmning (assoc ny-dæmning :ud-kø (:ud-kø (last (:dæmninger @system))))
     ny-dæmning (assoc ny-dæmning :sidste? true)
     ny-dæmning (assoc ny-dæmning :ind-kø ny-kø)

     ]
    (swap! system assoc-in [:dæmninger 1 :sidste?] false)   ;;WS
    (swap! system assoc-in [:dæmninger 1 :ud-kø] (ny-dæmning :ind-kø))
    (swap! system update :dæmninger conj ny-dæmning)

    (daemning/byg-dæmning! ny-dæmning)
    (daemning/byg-dæmning! (get-in @system [:dæmninger 1]))
    )
  (timer/start)                                             ;; Start tiden igen
  )

(defn tilføj
  []
  (tilføj-dæmning!
    {
     :navn         "NyDømning"
     :ventetid     3
     :fejl-procent 5
     :kø-størrelse 12
     :fejl-kø      fejl-kø
     :slut-liste   slut-liste
     }
    )
  )

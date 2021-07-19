(ns hackaton.daemning
  (:require [hackaton.timer :as timer]
            )
  (:import (java.util UUID)
           (java.time Instant)
           ))
"https://www.tutorialspoint.com/clojure/clojure_watchers.htm "
"https://cljdoc.org/d/shams/priority-queue/0.1.2/doc/readme"

"Dette er en priority-queue som sorterer på de elementer som har en høj error værdi"

"Man kan indsætte i køen med conj som

(def min-kø (conj min-kø (værdi)))

pop vil returnerer den nye kø, så hvis man skal have data ud, skal man huske at peek først.

(peek min-kø)

(def min-kø (pop min-kø))
"

(defn skovarbejder
  [_ navn ud-kø ind-kø _]
  (let [
        kø-størrelse 12
        funktion (fn [key atom old-state new-state]
                   (println "kø størrelse:" navn (count @ud-kø))
                   (println "fejlkø størrelse:" navn (count @ind-kø))
                   (if (< (count @ud-kø) kø-størrelse)
                     (do
                       (if (peek @ind-kø)
                         (do
                           (let [træ (peek @ind-kø)]
                             (println "Gammelt træ " træ)
                             (swap! ud-kø conj træ)
                             (swap! ind-kø pop)
                             ))
                         (do
                           (let [træ (hackaton.skov/fæld-træ 0, new-state)]
                             (swap! ud-kø conj træ)
                             ))
                         )
                       )
                     (do
                       (println "Køen er fuld!")
                       (println @ud-kø))
                     ))
        ]
    (add-watch timer/tick :skovarbejderen funktion)
    ))


(defn dæmning
  [ventetid navn ud-kø ind-kø fejl-kø]
  (let [
        fejl-procent 0.05
        kø-størrelse 12
        arbejde (atom nil)

        funktion (fn [key atom old-state new-state]
                   (println "ud-kø størrelse:" navn (count @ud-kø))
                   (println "ind-kø størrelse:" navn (count @ind-kø))
                   (println @arbejde)
                   (if @arbejde
                     (let [vent (:vent @arbejde)]
                       (println "vent" vent)
                       (if (> vent 1)                       ;; Arbejdet er endnu ikke udført, vent lidt mere.
                         (do
                           (println navn "arbejder på " @arbejde)
                           (swap! arbejde update :vent dec)  ;; Tæl ventetiden ned.

                           )
                         (do
                           (let [
                                 træ (:træ arbejde)
                                 _ (println træ)
                                 _ (println (:log træ))
                                 træ (update træ :log conj {:event      "Dæmning 1"
                                                            :tick       new-state
                                                            :accesstime (Instant/now)})
                                 ]
                             (swap! ud-kø conj træ)
                             (reset! arbejde nil)
                             ))
                         )
                       )

                     (do
                       (if (< (count @ud-kø) kø-størrelse)
                         (do
                           (if (peek @ind-kø)
                             (let [
                                   træ (peek @ind-kø)
                                   ]
                               (reset! arbejde {:vent ventetid :træ træ})
                               (swap! ind-kø pop)
                               )
                             (println "Queue empty, resting")
                             )
                           )
                         (do
                           (println "Køen er fuld!")
                           (println @ud-kø))
                         ))
                     ))
        ]
    (add-watch timer/tick :d1 funktion)))


(defn ticker-har-ticket
  [key atom old-state new-state]
  (println "Uret har ticket")
  (println "Før værdi" old-state)
  (println "Ny værdi" new-state)
  (println "atom" atom)
  (println "key" key)
  (println "************************")
  )


(defn example []
  (add-watch timer/tick :watcher
             ticker-har-ticket))

(defn start-dæmning []
  (add-watch timer/tick :kø
             ticker-har-ticket)
  )

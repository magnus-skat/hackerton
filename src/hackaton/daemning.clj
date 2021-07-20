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


(defn- skab-skovarbejder-funktion
  [navn ud-kø ind-kø kø-størrelse]
  (let [funktion (fn [key atom old-state new-state]
                   (println "udkø størrelse:" navn (count @ud-kø))
                   (println "indkø størrelse:" navn (count @ind-kø))
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
                             ))))
                     (do
                       (println "Køen er fuld!")
                       (println @ud-kø))))]

    funktion
    ))


(defn skovarbejder
  [_ navn ud-kø ind-kø _]
  (let [
        kø-størrelse 12
        funktion (skab-skovarbejder-funktion navn ud-kø ind-kø kø-størrelse)]
    (add-watch timer/tick :skovarbejderen funktion)
    ))

(defn- opdater-log
  [træ besked]
  (let [træ (update træ :log conj besked)]
    træ
    )
  )


(defn- skab-daemning-funktion
  [navn ud-kø ind-kø kø-størrelse ventetid fejl-procent]
  (let [arbejde (atom nil)
        funktion (fn
                   [key atom old-state new-state]
                   (if (nil? @arbejde)
                     (if (< (count @ud-kø) kø-størrelse)
                       (do
                         (if (peek @ind-kø)
                           (do
                             (let [
                                   træ (peek @ind-kø)       ;; Find det næste træ
                                   _ (swap! ind-kø pop)     ;; Fjern det fra køen.
                                   træ (opdater-log træ {:event      (str navn " arbejde startet")
                                                         :tick       new-state
                                                         :accesstime (Instant/now)})
                                   ]
                               (reset! arbejde {:ventetid ventetid
                                                :træ      træ})

                               ))
                           (println "Queue empty, resting")))
                       (do
                         (println "Køen er fuld!")
                         (println @ud-kø))
                       )
                     (do
                       ;; Arbejde er not nil
                       (if (< 0 (:ventetid @arbejde))
                         (do
                           (println "Vi arbejder på " @arbejde)
                           (swap! arbejde update :ventetid dec) ;; Sæt ventetiden ned
                           )

                         (let [
                               træ (:træ @arbejde)
                               træ (assoc træ :sluttick new-state)
                               træ (opdater-log træ {:event      (str navn " arbejde sluttet")
                                                     :tick       new-state
                                                     :accesstime (Instant/now)})
                               ]
                           (println "Working on " træ)

                           (swap! ud-kø conj træ)
                           (reset! arbejde nil)
                           )))
                     ))]
    funktion
    )
  )


(defn dæmning
  [ventetid navn ud-kø ind-kø fejl-kø]
  (let [
        fejl-procent 0.05
        kø-størrelse 12
        funktion (skab-daemning-funktion navn ud-kø ind-kø kø-størrelse ventetid fejl-procent)
        ]
    (add-watch timer/tick (keyword navn) funktion)))

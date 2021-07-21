(ns hackaton.daemning
  (:require [hackaton.timer :as timer]
            )
  (:import (java.util UUID)
           (java.time Instant)
           ))


(defn- skab-skovarbejder-funktion
  [navn ud-kø ind-kø kø-størrelse]
  (let [funktion (fn [key atom old-state new-state]
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
  [_ navn ind-kø ud-kø _ _]
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
  [navn ud-kø ind-kø kø-størrelse ventetid fejl-procent sidste]
  (let [arbejde (atom nil)
        funktion (fn
                   [key atom old-state new-state]
                   "agents skal have fire argumenter.
                    key er navnet som den fik ved oprettelsen
                    atom, atomeet som er ændret
                    old-state, den gamle værdi
                    new-state den nye værdi
                   "
                   (if (nil? @arbejde)
                     (if (or sidste (< (count @ud-kø) kø-størrelse))
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
                           (println navn " arbejder på " @arbejde)
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
  [{:keys [ventetid navn ind-kø ud-kø fejl-kø sidste]}]
  (let [
        fejl-procent 0.05
        kø-størrelse 12
        funktion (skab-daemning-funktion navn ud-kø ind-kø kø-størrelse ventetid fejl-procent sidste)
        ]
    (add-watch timer/tick (keyword navn) funktion)))

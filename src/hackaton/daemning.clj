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
                           (let [træ (peek @ind-kø)
                                 træ (assoc træ :error 0)]

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

(defn- fejl?
  [fejl-procent]
  (<= (rand-int 100) fejl-procent))

(defn- arbejd
  [{:keys [navn ud-kø fejl-kø fejl-liste fejl-procent]} arbejde new-state ]
  (if (< 0 (:ventetid @arbejde))
    (do
      (println navn " arbejder på " @arbejde)
      (swap! arbejde update :ventetid dec)                  ;; Sæt ventetiden ned
      )

    (if (fejl? fejl-procent)
      (
        let [
             træ (:træ @arbejde)
             træ (assoc træ :sluttick new-state)
             træ (assoc træ :error 10)
             træ (opdater-log træ {:event      (str navn " arbejdet fejlede")
                                   :tick       new-state
                                   :accesstime (Instant/now)})
             ]
        (println "Arbejdet fejlet")
        (swap! fejl-kø conj træ)
        (swap! fejl-liste conj {
                                :tick new-state
                                :træ  (:id træ)
                                })

        (reset! arbejde nil)
        )
      (let [
            træ (:træ @arbejde)
            træ (assoc træ :sluttick new-state)
            træ (opdater-log træ {:event      (str navn " arbejde sluttet")
                                  :tick       new-state
                                  :accesstime (Instant/now)})
            ]
        (swap! ud-kø conj træ)
        (reset! arbejde nil)
        ))
    )
  )

(defn- skab-daemning-funktion
  [{:keys [navn ud-kø ind-kø ventetid sidste? kø-størrelse] :as dæmning} ]
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
                     (if (or sidste? (< (count @ud-kø) kø-størrelse))
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
                         )
                       )
                     (do
                       ;; Arbejde er not nil
                       (arbejd dæmning arbejde new-state)
                       )
                     ))]
    funktion
    )
  )

(defn dæmning
  [dæmning]
  (let [
        funktion (skab-daemning-funktion dæmning)
        ]
    (add-watch timer/tick (keyword (:navn dæmning)) funktion)))

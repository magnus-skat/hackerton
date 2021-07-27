(ns hackaton.daemning
  (:require [hackaton.timer :as timer]
            )
  (:import (java.util UUID)
           (java.time Instant)
           ))

(defn- skab-skovarbejder-funktion
  [_ ud-kø ind-kø kø-størrelse]
  (let [funktion (fn [_ _ _ new-state]
                   (if (< (count @ud-kø) kø-størrelse)
                     (do
                       (if (peek @ind-kø)
                         (do
                           (let [træ (peek @ind-kø)
                                 træ (assoc træ :error 0)]
                             (swap! ud-kø conj træ)
                             (swap! ind-kø pop)
                             ))

                         (swap! ud-kø conj (hackaton.skov/fæld-træ 0, new-state))
                         ))
                     (println "Køen er fuld!")))]
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
  "Det er her selve arbejdet udføres på et træ Hvis tiden er gået smides det på
   den næste kø, ellers venter vi lidt længere"
  [{:keys [navn ud-kø fejl-kø fejl-liste fejl-procent ind-kø]} arbejde new-state]
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
        (swap! ind-kø pop)                                  ;; Fjern træet fra ind-køen.
        (swap! ud-kø conj træ)                              ;; Tilføj det til næste kø

        (reset! arbejde nil)
        ))))

(defn- skab-daemning-funktion
  "Returnerer den funktion, som skal køres som en watcher"
  [{:keys [navn ud-kø ind-kø ventetid sidste? kø-størrelse] :as dæmning}]
  (let [arbejde (atom nil)
        funktion (fn
                   [key atom old-state new-state]
                   "agents skal have fire argumenter.
                    key er navnet som den fik ved oprettelsen
                    atom, atomeet som er ændret
                    old-state, den gamle værdi
                    new-state den nye værdi
                   "
                   (if (nil? @arbejde)                      ;; Hvis der ikke er noget arbejde igang så...
                     (if (or sidste? (< (count @ud-kø) kø-størrelse)) ;; Hvis det er den sidste dæmning eller hvis der er plads på næste kø
                       (if (peek @ind-kø)                   ;; Hvis der er noget på ind-køen
                         (let [
                               træ (peek @ind-kø)           ;; Find det næste træ

                               træ (opdater-log træ {:event      (str navn " arbejde startet")
                                                     :tick       new-state
                                                     :accesstime (Instant/now)}) ;; Indsæt data ind i loggen
                               ]
                           (reset! arbejde {:ventetid ventetid :træ træ})) ;; Lav noget 'arbejde'
                         (println "Queue empty, resting"))  ;; Ind-Køen var tom

                       (println "Køen er fuld!"))           ;; Ud-køen var fuld.
                     (arbejd dæmning arbejde new-state)))]  ;; Der var arbejde der skulle laves
    funktion))

(defn byg-dæmning!
  [dæmning]
  (println "byg dæmninger")
  (let [funktion (skab-daemning-funktion dæmning)]
    (add-watch timer/tick (keyword (:navn dæmning)) funktion)))

(defn update-ventetid!
  "Updaterer ventetiden/arbejdstiden for en bestemt dæmning"
  [system nummer ventetid]
  (timer/stop)                                              ;; stop tiden for en sikkerheds skyld
  (Thread/sleep @timer/ventetid)                            ;; vent på at alle tråde er færdige
  (swap! system assoc-in [:dæmninger nummer :ventetid] ventetid) ;; opdater ventetiden på dæmningen
  (byg-dæmning! ((@system :dæmninger) nummer))              ;; gen-start funktionen der kører på dæmningen
  (timer/start)                                             ;; Start tiden igen
  )


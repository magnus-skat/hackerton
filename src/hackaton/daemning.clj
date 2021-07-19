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
  [ventetid navn kø fejl-kø]
  (let [
        id (UUID/randomUUID)
        kø-størrelse ventetid

        funktion (fn [key atom old-state new-state]
                   (println "kø størrelse:" (count @kø))
                   (println "fejlkø størrelse:" (count @fejl-kø ))
                   (if (< (count @kø) kø-størrelse)
                     (do
                       (if (peek @fejl-kø)
                         (do
                           (let [træ (peek @fejl-kø)]
                             (println "Gammelt træ " træ)
                             (swap! kø conj træ)
                             (swap! fejl-kø pop)
                             ))
                         (do
                           (let [træ (hackaton.skov/fæld-træ 0)]
                             (swap! kø conj træ)
                             ))
                         )
                       )
                     (do
                       (println "Køen er fuld!")
                       (println @kø))
                     ))
        ]

    (add-watch timer/tick :kø
               funktion)
    ))


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

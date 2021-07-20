(ns hackaton.timer
  )

"Ideen med timeren er, at den hvert sekund, eller måske bare hvert femte,  sender et 'tick' ud til systemet, som
andre tråde så abonnerer på via en watcher, og når der er kommet et 'tick', vil de så udføre deres opgave og så sove indtil næste 'tick' "

"https://www.tutorialspoint.com/clojure/clojure_watchers.htm "

(def tick (atom 0))
(def ventetid (atom 1000)) ;; Antal millisekunder som timeren skal sove, inden den sendet et nyt tick ud

(defn start-timer
  []
  (while (< @tick 250)
    (do
      (Thread/sleep @ventetid)
      (swap! tick inc)
      )
    )
  )


(defn langsommere
  []
  (swap! @ventetid * 2)
  )


(defn langsommere
  []
  (swap! @ventetid / 2)
  )

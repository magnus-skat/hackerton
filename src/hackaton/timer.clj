(ns hackaton.timer
  )

"Ideen med timeren er, at den hvert sekund, eller måske bare hvert femte,  sender et 'tick' ud til systemet, som
andre tråde så abonnerer på, og når der er kommet et 'tick', vil de så udføre deres opgave og så sove indtil næste 'tick' "


(def tick (atom 0))


(def ventetid (atom 1000)) ;; Antal millisekunder som timeren skal sove, inden den sendet et nyt tick ud

(defn start-timer
  [x]
  (while (< @tick 10000)
    (do
      (sleep @ventetid)
      (swap! tick inc)
      )
    )
  )

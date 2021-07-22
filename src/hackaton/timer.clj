(ns hackaton.timer
  )

"Ideen med timeren er, at den hvert sekund, eller måske bare hvert femte,  sender et 'tick' ud til systemet, som
andre tråde så abonnerer på via en watcher, og når der er kommet et 'tick', vil de så udføre deres opgave og så sove indtil næste 'tick' "

"https://www.tutorialspoint.com/clojure/clojure_watchers.htm "

(def tick (atom 0))
(def pause (atom false))                                    ;; Hvis True, skal timere bare noppe
(def ventetid (atom 1000))                                  ;; Antal millisekunder som timeren skal sove, inden den sendet et nyt tick ud

(defn start-timer
  ([] (start-timer 250))
  ([stop-tick]
   (while (< @tick stop-tick)
     (if @pause                                             ;; It's time for a nop.
       (Thread/sleep 1000)
       (do
         (Thread/sleep @ventetid)
         (swap! tick inc)
         )
       )
     ))
  )

;;; Disse tre funktioner, ændre tiden mellem ticks. Kan kaldes fra REPL, med
;; (timer/langsomt) Virker også når systemet kører.

(defn start-stop
  []
  (swap! pause not)
  (println "Starter eller stopper timeren")
  )

(defn start
  []
  (reset! pause false)
  (println "Starter timeren")
  )

(defn stop
  []
  (reset! pause true)
  (println "Stopper timeren")
  )

(defn langsomt
  []
  (reset! ventetid 5000)
  )

(defn hurtigt
  []
  (reset! ventetid 500)
  )

(defn normal
  []
  (reset! ventetid 1000)
  )

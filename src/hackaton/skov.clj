(ns hackaton.skov
  (:import (java.util UUID)
           (java.time Instant)
           ))

(defn fæld-træ
  "I don't do a whole lot.
  But I do create a træstamme, and stamps it with a timestamp or three"
  [x]

  (def træstamme
    {:id       (UUID/randomUUID)
     :starttid (Instant/now)
     :sluttid  (Instant/now)
     :log      [
                {
                 :event      "fældet"
                 :accesstime (Instant/now)}]})
  træstamme)
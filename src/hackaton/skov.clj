(ns hackaton.skov
  (:import (java.util UUID)
           (java.time Instant)
           ))

(defn fæld-træ
  "I don't do a whole lot.
  But I do create a træstamme, and stamps it with a timestamp or three
  x er den prioritet som stammen får. Nu højere værdi, nu hurtigere bliver den spyttet ud af køen.
  "
  [fejlkode tick]
  {
   ;; id er bare måske ikke så vigtig lige nu, men måske senere, kan det bruges til at logge ting.
   ;; RandomUUID burde være god nok til altid genererer et uniksnummer
   :id       (UUID/randomUUID)
   :starttid (Instant/now)                                  ;; Måske dette skal være et tick og ikke et tidspunkt
   :starttick tick
   :sluttid  nil
   :sluttick nil
   :log      [
              {:event      "fældet"                         ;; Tænkt som et sted, hvor de forskellige dæmninger skriver hvornår de har håndteret stammen.
               :tick       tick
               :accesstime (Instant/now)}]
   ;; Error er sådan vi afgører om et træ skal foran i køen. Hvis error er sat skal køen, vælge denne stamme først
   :error    fejlkode})
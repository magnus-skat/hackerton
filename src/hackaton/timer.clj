(ns hackaton.timer
  )

"Ideen med timeren er, at den hvert sekund, eller måske bare hvert femte,  sender et 'tick' ud til systemet, som
andre tråde så abonnerer på, og når der er kommet et 'tick', vil de så udføre deres opgave og så sove indtil næste 'tick' "

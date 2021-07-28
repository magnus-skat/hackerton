(ns web.app
  (:require
    [reagent.core :as r]
    [reagent.dom :as rdom]
    [hackaton.core :as core]
    )
  )

(defn miniapp []
  [:h1 "Hackaton webpage"]
  [:div "Hej mor"]
  )


(defn ^:export run []
  (rdom/render [miniapp] (js/document.getElementById "app")))

(defn ^:export reload []
  (js/console.log "reload...")
  (run) 2)

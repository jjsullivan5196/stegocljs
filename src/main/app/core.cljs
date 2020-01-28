(ns app.core
  (:require [reagent.core :as rg]
            [components.canvas :refer [CanvasTransform]]))

(defn ui
  [] 
  [CanvasTransform {:transform (partial map (fn [[r g b a]] [g r b a]))
                    :url "https://upload.wikimedia.org/wikipedia/commons/a/af/Tux.png"}])

(defn render
  []
  (rg/render [ui] (.getElementById js/document "app")))

(defn init
  []
  (render))

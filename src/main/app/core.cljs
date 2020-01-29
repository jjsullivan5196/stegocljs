(ns app.core
  (:require [components.canvas :refer [CanvasTransform]]
            [helix.core :refer [defnc $]]
            [helix.dom :as d]
            ["react-dom" :as react-dom]))

(defnc ui
  [] 
  ($ CanvasTransform {:transform (partial map (fn [[r g b a]] [g r b a]))
                      :url "https://upload.wikimedia.org/wikipedia/commons/a/af/Tux.png"}))

(defn render
  []
  (react-dom/render ($ ui)
                    (.getElementById js/document "app")))

(defn init
  []
  (render))

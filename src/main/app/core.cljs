(ns app.core
  (:require [components.canvas :refer [CanvasTransform]]
            [hx.react :as hx :refer [defnc]]
            ["react-dom" :as react-dom]))

(defnc ui
  [] 
  [CanvasTransform {:transform (partial map (fn [[r g b a]] [g r b a]))
                    :url "https://upload.wikimedia.org/wikipedia/commons/a/af/Tux.png"}])

(defn render
  []
  (react-dom/render (hx/f [ui])
                    (.getElementById js/document "app")))

(defn init
  []
  (render))

(ns stego.core
  (:require [stego.components.canvas :refer [CanvasTransform]]
            [stego.components.util :as cm]
            [stego.util :as u]
            [helix.core :refer [defnc $]]
            [helix.dom :as d]
            ["react-dom" :as react-dom]))

(def img-url "https://upload.wikimedia.org/wikipedia/commons/a/af/Tux.png")

(defnc ui
  []
  (d/div
    (when-let [blob (cm/use-thunk u/fetch-blob! img-url)]
      ($ CanvasTransform
         {:transform (partial map (fn [[r g b a]] [g r b a]))
          :img-source blob}))))

(defn render
  []
  (react-dom/render
    ($ ui)
    (.getElementById js/document "app")))

(defn init
  []
  (render))

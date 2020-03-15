(ns stego.core
  (:require [stego.components.canvas :refer [CanvasTransform]]
            [stego.components.util :as cm]
            [stego.util :as u]
            [helix.core :refer [defnc $]]
            [helix.dom :as d]
            ["react-dom" :as react-dom]))

(def img-url
  "https://upload.wikimedia.org/wikipedia/commons/8/83/Con_%C4%91%C6%B0%E1%BB%9Dng_h%E1%BA%A1nh_ph%C3%BAc.jpg")

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

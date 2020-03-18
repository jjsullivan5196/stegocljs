(ns stego.core
  (:require [stego.components.glcanvas :refer [GlCanvas]]
            [stego.components.util :as cm]
            [stego.util :as u]
            [helix.core :refer [defnc $]]
            [helix.dom :as d]
            ["react-dom" :as react-dom]))

(def img-url "images/viet.jpg")

(defnc ui
  []
  (d/div
    (when-let [blob (cm/use-thunk u/fetch-blob! img-url)]
      ($ GlCanvas {:img-source blob}))))

(defn render
  []
  (react-dom/render
    ($ ui)
    (.getElementById js/document "app")))

(defn init
  []
  (render))

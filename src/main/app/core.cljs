(ns app.core
  (:require [components.canvas :refer [WrapBitmap]]
            [util.core :as c]
            [clojure.core.async :as a]
            [helix.core :refer [defnc $]]
            [helix.hooks :as hooks]
            [helix.dom :as d]
            ["react-dom" :as react-dom]))

(def img-url "https://upload.wikimedia.org/wikipedia/commons/a/af/Tux.png")

(defnc ui
  []
  (let [[blob set-blob] (hooks/use-state nil)]
    (hooks/use-effect
      :once (a/take! (c/fetch-blob! img-url) set-blob))
    (if blob
      ($ WrapBitmap {:transform (fn [[r g b a]] [g r b a])
                     :img-source blob})
      (d/div))))

(defn render
  []
  (react-dom/render
    ($ ui)
    (.getElementById js/document "app")))

(defn init
  []
  (render))

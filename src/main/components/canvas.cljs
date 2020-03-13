(ns components.canvas
  "Canvas-related react components."
  (:require [util.core :as c]
            [util.image :as img]
            [clojure.core.async :as a]
            [helix.core :refer [defnc $]]
            [helix.hooks :as hooks]
            [helix.dom :as d]))

(defn do-conversion
  [transform img]
  (let [{:keys [width height pixels] :as data}
        (update img :pixels (partial map transform))]
    {:width width
     :height height
     :data (img/->ImageData data)}))

(defnc CanvasTransform
  "Get image from `img`, apply `transform` to the RGBA pixels and display the result."
  [{:keys [transform img]}]
  (let [{:keys [width height data]}
        (do-conversion transform img)]

    (d/div
      (d/canvas
        {:ref
         (fn [canvas]
           (when canvas
             (set! (.-width canvas) width)
             (set! (.-height canvas) height)
             (.. canvas (getContext "2d") (putImageData data 0 0))))}))))

(defnc WrapBitmap
  [{:keys [transform img-source] :as props}]
  (let [[bmp set-bmp] (hooks/use-state nil)]
    (hooks/use-effect
      [img-source]
      (a/take! (img/load-pixels! img-source) set-bmp))

    (if bmp
      ($ CanvasTransform {:transform transform :img bmp})
      (d/div))))

(ns components.canvas
  "Canvas-related react components."
  (:require [components.util :as cm]
            [util.canvas :as cn]
            [util.image :as im]
            [helix.core :refer [defnc $]]
            [helix.hooks :as hooks]
            [helix.dom :as d]))

(defnc CanvasTransform
  "Get image from `img`, apply `transform` to the RGBA pixels and display the result."
  [{:keys [transform img-source]}]
  (d/div
    (let [img (cm/use-thunk im/load-pixels! img-source)
          canvas-ref (hooks/use-ref nil)]
      (when img
        (let [data (-> (update img :pixels transform) (im/->ImageData))]
          (when-let [canvas @canvas-ref] (cn/draw! canvas data))
          (d/canvas
            {:ref
             #(when %
                (cn/draw! % data)
                (reset! canvas-ref %))}))))))

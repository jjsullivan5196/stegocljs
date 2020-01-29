(ns components.canvas
  "Canvas-related react components."
  (:require [util.core :refer [use-ref!]]
            [util.canvas :as cn]
            [helix.core :refer [defnc $]]
            [helix.hooks :as hooks]
            [helix.dom :as d]))

(defnc CanvasTransform
  "Get image from `url`, apply `transform` to the RGBA pixels and display the result."
  [{:keys [transform url]}]
  (let [!cref (hooks/use-ref nil)
        [data set-data] (hooks/use-state nil)]

    (hooks/use-effect
      [url]
      (cn/open-image! url set-data @!cref))

    (hooks/use-effect
      [transform data]
      (when data
        (cn/transform-canvas! transform data @!cref)))

    (d/div
      (d/canvas {:ref (use-ref! cn/make-ref !cref)}))))

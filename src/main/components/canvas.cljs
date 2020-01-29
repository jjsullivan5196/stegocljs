(ns components.canvas
  "Canvas-related react components."
  (:require [util.core :refer [use-ref!]]
            [util.canvas :as cn]
            [hx.react :as hx :refer [defnc]]
            [hx.hooks :as hooks]))

(defnc CanvasTransform
  [{:keys [transform url]}]
  (let [!cref (hooks/useIRef nil)
        [data set-data] (hooks/useState nil)]

    (hooks/useEffect
      #(cn/open-image! url set-data @!cref)
      [url])

    (hooks/useEffect
      (fn []
        (when data
          (cn/transform-canvas! transform data @!cref)))
      [transform data])

    [:div [:canvas {:ref (use-ref! cn/make-ref !cref)}]]))

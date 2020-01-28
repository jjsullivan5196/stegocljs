(ns components.canvas
  "Canvas-related react components."
  (:require [util.core :refer [use-ref!]]
            [util.canvas :as cn]
            [reagent.core :as rg]))

(defn CanvasTransform
  "Get image from `url`, apply `transform` to the RGBA pixels and display the result."
  [{:keys [transform url]}]
  (let [!canvas (atom nil)
        !data   (rg/atom nil)
        cref    (comp
                  (partial cn/open-image! url (use-ref! !data))
                  cn/make-ref)]

    (fn []
      (when-let [dat @!data] (cn/transform-canvas! transform dat @!canvas))

      [:div
       [:canvas
        {:ref (use-ref! !canvas cref)}]])))

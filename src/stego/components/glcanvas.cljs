(ns stego.components.glcanvas
  "OpenGL canvas experiment"
  (:require [stego.canvas.util :as cu]
            [stego.canvas.drawing :as dr]
            [stego.gl.image :as im]
            [stego.components.util :as cm]
            [helix.core :refer [defnc $]]
            [helix.hooks :as hooks]
            [helix.dom :as d]))

(defn draw-callback
  [{:keys [element context]} img]
  (im/draw-image context {:image img}))

(defnc GlCanvas
  [{:keys [img-source]}]
  (d/div
    (let [img (cm/use-thunk cu/new-bitmap img-source)
          canvas-ref (hooks/use-ref nil)]
      (when img
        (when-let [canvas @canvas-ref] (draw-callback canvas img))
        (d/canvas
          {:width 400
           :height 400
           :ref
           #(when %
              (dr/set-dimensions! % (dr/dimensions img))
              (let [r {:element % :context (im/new-image-context %)}]
                (reset! canvas-ref r)
                (draw-callback r img)))})))))

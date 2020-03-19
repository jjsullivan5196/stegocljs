(ns stego.components.glcanvas
  "OpenGL canvas experiment"
  (:require [stego.canvas.util :as cu]
            [stego.canvas.drawing :as dr]
            [stego.gl.image :as im]
            [stego.components.util :as cm]
            [play-cljc.gl.core :as glc]
            [helix.core :refer [defnc $]]
            [helix.hooks :as hooks]
            [helix.dom :as d]))

(defn ->image [bmp]
  (-> bmp
      (dr/dimensions)
      (assoc :data bmp)))

(defnc GlCanvas
  [{:keys [img-source]}]
  (d/div
    (let [bmp (cm/use-thunk cu/new-bitmap img-source)
          canvas-ref (hooks/use-ref nil)]
      (when bmp
        (let [{:keys [width height] :as img} (->image bmp)]
          (when-let [canvas @canvas-ref]
            (let [image-bound (im/image-setup canvas img)]
              (reset! canvas-ref image-bound)
              (im/draw-image! image-bound)))
          (d/canvas
            {:width width
             :height height
             :ref
             #(when %
                (let [canvas (-> (.getContext % "webgl2")
                                 (glc/->game)
                                 (im/image-setup img))]
                  (reset! canvas-ref canvas)
                  (im/draw-image! canvas)))}))))))

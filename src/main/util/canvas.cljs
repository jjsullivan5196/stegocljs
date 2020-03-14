(ns util.canvas
  "Methods for working directly with the Canvas API."
  (:require [util.core :as c]))

(defprotocol Drawable
  (dimensions [data] "Get dimensions of drawable object.")
  (put-data! [data context] "Put object onto canvas."))

(defn set-dimensions!
  "Set canvas dimensions."
  [canvas {:keys [width height]}]
  (set! (.-width canvas) width)
  (set! (.-height canvas) height))

(defn draw!
  "Draw `img` onto `canvas`. Return 2d drawing context."
  [canvas img]
  (set-dimensions! canvas (dimensions img))
  (let [context (.getContext canvas "2d")]
    (put-data! img context)
    context))

(defn drawing-data
  "Create a drawing with a temporary canvas. Return result image data."
  [img]
  (let [{:keys [width height]} (dimensions img)
        canvas (js/OffscreenCanvas. width height)]
    (.. (draw! canvas img)
        (getImageData 0 0 width height))))

(def image-types [js/Image js/ImageBitmap])

(defn image-dimensions
  [img]
  {:width (.-width img)
   :height (.-height img)})

(extend-type js/ImageData
  Drawable
  (dimensions [self] (image-dimensions self))
  (put-data! [self context]
    (.putImageData context self 0 0)))

(c/extend-types
  image-types

  Drawable
  (dimensions [self] (image-dimensions self))
  (put-data!
    [self canvas]
    (.drawImage canvas self 0 0)))

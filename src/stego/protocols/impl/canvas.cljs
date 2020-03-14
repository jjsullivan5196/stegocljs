(ns stego.protocols.impl.canvas
  (:require [stego.protocols.canvas :refer [Drawable]]
            [stego.protocols.impl.common :refer [image-types]]
            [stego.util :as u]))

(defn image-dimensions
  [img]
  {:width (.-width img)
   :height (.-height img)})

(extend-type js/ImageData
  Drawable
  (dimensions [self] (image-dimensions self))
  (put-data! [self context]
    (.putImageData context self 0 0)))

(u/extend-types
  image-types

  Drawable
  (dimensions [self] (image-dimensions self))
  (put-data!
    [self canvas]
    (.drawImage canvas self 0 0)))

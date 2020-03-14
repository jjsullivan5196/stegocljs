(ns stego.canvas.image
  (:require [stego.canvas.drawing :refer [Drawable dimensions put-data! drawing-data]]
            [stego.canvas.common :refer [image-types]]
            [stego.util :as u]))

(defprotocol ToPixels
  (->PixelData [data] "Convert some object to a `Pixels` record."))

(defprotocol ToImageData
  (->ImageData [data] "Create `ImageData` from some object."))

(defrecord Pixels [width height pixels]
  Drawable
  (dimensions [self]
    (dissoc self :pixels))
  (put-data! [self context]
    (-> self (->ImageData) (put-data! context)))

  ToPixels
  (->PixelData [self] self)

  ToImageData
  (->ImageData [self]
    (let [arr (-> pixels (flatten) (js/Uint8ClampedArray.from))]
      (js/ImageData. arr width height))))

(def pixel-stride
  "Byte length of RGBA canvas pixel" 4)

(extend-type js/ImageData
  ToImageData
  (->ImageData [self] self)

  ToPixels
  (->PixelData [self]
    (let [pixels (->> (.-data self) (array-seq) (partition pixel-stride))
          dims (dimensions self)]
      (map->Pixels (assoc dims :pixels pixels)))))

(u/extend-types
  image-types
  ToImageData
  (->ImageData [self] (drawing-data self))

  ToPixels
  (->PixelData [self] (-> self (->ImageData) (->PixelData))))

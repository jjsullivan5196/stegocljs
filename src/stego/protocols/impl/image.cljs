(ns stego.protocols.impl.image
  (:require [stego.protocols.image :refer [Pixels ToPixels ToImageData ->PixelData ->ImageData map->Pixels]]
            [stego.protocols.canvas :refer [Drawable dimensions put-data!]]
            [stego.canvas.core :refer [drawing-data]]
            [stego.protocols.impl.common :refer [image-types]]
            [stego.util :as u]))

(def pixel-stride
  "Byte length of RGBA canvas pixel" 4)

(extend-type Pixels
  Drawable
  (dimensions [self]
    (dissoc self :pixels))
  (put-data! [self context]
    (-> self (->ImageData) (put-data! context)))

  ToPixels
  (->PixelData [self] self)

  ToImageData
  (->ImageData [{:keys [width height pixels] :as self}]
    (let [arr (-> pixels (flatten) (js/Uint8ClampedArray.from))]
      (js/ImageData. arr width height))))

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

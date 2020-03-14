(ns util.image
  "Specific functions for image data."
  (:require [util.canvas :as cn]
            [util.core :as c]
            [clojure.core.async :as a]))

(defprotocol ToPixels
  (->PixelData [data] "Convert some object to a `Pixels` record."))

(defprotocol ToImageData
  (->ImageData [data] "Create `ImageData` from some object."))

(defn new-bitmap
  "Returns a channel that delivers an `ImageBitmap` object created from some
  image-like object `img`. Optionally use transducer `xf` to operate on the
  bitmap."
  ([img] (new-bitmap img (map identity)))
  ([img xf]
   (let [data-chan (a/promise-chan xf)
         data-promise (js/createImageBitmap img)]
     (.then data-promise #(a/put! data-chan %))
     data-chan)))

(defn load-pixels!
  "Get `PixelData` record for some image-containing `data`."
  [data]
  (new-bitmap data (map ->PixelData)))

(defrecord Pixels [width height pixels]
  cn/Drawable
  (dimensions [self]
    (dissoc self :pixels))
  (put-data! [self context]
    (-> self (->ImageData) (cn/put-data! context)))

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
          dims (cn/dimensions self)]
      (map->Pixels (assoc dims :pixels pixels)))))

(c/extend-types
  cn/image-types
  ToImageData
  (->ImageData [self] (cn/drawing-data self))

  ToPixels
  (->PixelData [self] (-> self (->ImageData) (->PixelData))))

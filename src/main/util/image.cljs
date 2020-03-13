(ns util.image
  "Specific functions for image data."
  (:require [util.canvas :as cn]
            [clojure.core.async :as a]))

(defrecord Pixels [width height pixels])

(defmulti ->PixelData
  "Convert some object to a `Pixels` record."
  type :hierarchy #'cn/image-traits)

(defmulti ->ImageData
  "Create `ImageData` from some object."
  type :hierarchy #'cn/image-traits)

(defmethod util.canvas/dimensions Pixels
  [data]
  (dissoc data :pixels))

(defmethod util.canvas/put-data! Pixels
  [canvas data]
  (->> data (->ImageData) (cn/put-data! canvas)))

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

(def pixel-stride
  "Byte length of RGBA canvas pixel" 4)

(defmethod ->PixelData :default [_] nil)

(defmethod ->PixelData Pixels [data] data)

(defmethod ->PixelData :util.canvas/image-like
  [img] (-> img (->ImageData) (->PixelData)))

(defmethod ->PixelData js/ImageData
  [data]
  (let [pixels (->> (.-data data) (array-seq) (partition pixel-stride))]
    (map->Pixels
      (assoc (cn/dimensions data) :pixels pixels))))

(defmethod ->ImageData :default [_] nil)

(defmethod ->ImageData js/ImageData [data] data)

(defmethod ->ImageData :util.canvas/image-like
  [img] (cn/drawing-data img))

(defmethod ->ImageData Pixels
  [data]
  (let [{:keys [width height pixels]} data
        arr (-> pixels (flatten) (js/Uint8ClampedArray.from))]
    (js/ImageData. arr width height)))

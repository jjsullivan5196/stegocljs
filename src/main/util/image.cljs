(ns util.image
  "Specific functions for image data"
  (:require [clojure.core.async :as a]))

(def pixel-stride
  "Byte length of RGBA canvas pixel" 4)

(defrecord Pixels [width height pixels])

(def image-like
  "Types that fulfill the javascript `CanvasImageSource` pseudointerface."
  (-> (make-hierarchy)
      (derive js/Image ::image-like)
      (derive js/ImageBitmap ::image-like)))

(defmulti ->ImageData
  "Create `ImageData` from some object."
  type :hierarchy #'image-like)

(defmethod ->ImageData js/ImageData
  [data] data)

(defmethod ->ImageData :default
  [_] nil)

(defmethod ->ImageData ::image-like
  [img]
  (let [width (.-width img)
        height (.-height img)
        context (.. (js/OffscreenCanvas. width height)
                    (getContext "2d"))]
    (.drawImage context img 0 0)
    (.getImageData context 0 0 width height)))

(defmethod ->ImageData Pixels
  [data]
  (let [{:keys [width height pixels]} data
        arr (-> pixels (flatten) (js/Uint8ClampedArray.from))]
    (js/ImageData. arr width height)))

(defmulti ->PixelData
  "Convert some object to a `Pixels` record"
  type :hierarchy #'image-like)

(defmethod ->PixelData Pixels
  [data] data)

(defmethod ->PixelData :default
  [_] nil)

(defmethod ->PixelData ::image-like
  [img]
  (-> img (->ImageData) (->PixelData)))

(defmethod ->PixelData js/ImageData
  [data]
  (let [pixels (->> (.-data data)
                    (array-seq)
                    (partition pixel-stride))]
    (map->Pixels
      {:width (.-width data)
       :height (.-height data)
       :pixels pixels})))

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
(ns stego.canvas.util
  "Image related utilities."
  (:require [stego.canvas.image :as im]
            [clojure.core.async :as a]))

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
  (new-bitmap data (map im/->PixelData)))

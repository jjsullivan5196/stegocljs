(ns stego.canvas.core
  "Utilities for working with Canvas API."
  (:require [stego.protocols.canvas :as cn]
            [stego.protocols.image :as im]
            [clojure.core.async :as a]))

(defn set-dimensions!
  "Set canvas dimensions."
  [canvas {:keys [width height]}]
  (set! (.-width canvas) width)
  (set! (.-height canvas) height))

(defn draw!
  "Draw `img` onto `canvas`. Return 2d drawing context."
  [canvas img]
  (set-dimensions! canvas (cn/dimensions img))
  (let [context (.getContext canvas "2d")]
    (cn/put-data! img context)
    context))

(defn drawing-data
  "Create a drawing with a temporary canvas. Return result image data."
  [img]
  (let [{:keys [width height]} (cn/dimensions img)
        canvas (js/OffscreenCanvas. width height)]
    (.. (draw! canvas img)
        (getImageData 0 0 width height))))

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

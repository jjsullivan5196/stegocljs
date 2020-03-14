(ns stego.protocols.image)

(defprotocol ToPixels
  (->PixelData [data] "Convert some object to a `Pixels` record."))

(defprotocol ToImageData
  (->ImageData [data] "Create `ImageData` from some object."))

(defrecord Pixels [width height pixels])

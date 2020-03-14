(ns stego.protocols.canvas)

(defprotocol Drawable
  (dimensions [data] "Get dimensions of drawable object.")
  (put-data! [data context] "Put object onto canvas."))

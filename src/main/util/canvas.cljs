(ns util.canvas
  "Methods for working directly with the Canvas API.")

(def image-traits
  "Attributes associated with image-like objects."
  (-> (make-hierarchy)
      ;; Objects which satify the `CanvasImageSource` pseudointerface
      (derive js/Image ::image-like)
      (derive js/ImageBitmap ::image-like)

      ;; Things with dimensions
      (derive ::image-like ::with-dims)
      (derive js/ImageData ::with-dims)))

(defmulti dimensions
  "Get dimensions of some image data."
  type :hierarchy #'image-traits)

(defmulti draw!
  "Draw something on a canvas."
  (fn [_ img] (type img)) :hierarchy #'image-traits)

(defn get-context
  "Shortcut for 2d drawing context."
  [canvas]
  (.getContext canvas "2d"))

(defn set-dimensions!
  "Set canvas dimensions."
  [canvas {:keys [width height]}]
  (set! (.-width canvas) width)
  (set! (.-height canvas) height))

(defn draw-setup!
  "Set canvas dimensions and return it's 2d drawing context."
  [canvas img]
  (set-dimensions! canvas (dimensions img))
  (get-context canvas))

(defn drawing-data
  "Create a drawing with a temporary canvas. Return result image data."
  [img]
  (let [{:keys [width height]} (dimensions img)
        canvas (js/OffscreenCanvas. width height)]
    (draw! canvas img)
    (.getImageData (get-context canvas) 0 0 width height)))

(defmethod dimensions :default [_] nil)

(defmethod dimensions ::with-dims
  [img]
  {:width (.-width img)
   :height (.-height img)})

(defmethod draw! :default [_ _] nil)

(defmethod draw! ::image-like
  [canvas img]
  (-> (draw-setup! canvas img)
      (.drawImage img 0 0)))

(defmethod draw! js/ImageData
  [canvas data]
  (-> (draw-setup! canvas data)
      (.putImageData data 0 0)))

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

(defmulti put-data!
  "Put something on a canvas."
  (fn [_ data] (type data)) :hierarchy #'image-traits)

(defn set-dimensions!
  "Set canvas dimensions."
  [canvas {:keys [width height]}]
  (set! (.-width canvas) width)
  (set! (.-height canvas) height))

(defn draw!
  "Draw `img` onto `canvas`. Return 2d drawing context."
  [canvas img]
  (set-dimensions! canvas (dimensions img))
  (let [context (.getContext canvas "2d")]
    (put-data! context img)
    context))

(defn drawing-data
  "Create a drawing with a temporary canvas. Return result image data."
  [img]
  (let [{:keys [width height]} (dimensions img)
        canvas (js/OffscreenCanvas. width height)]
    (.. (draw! canvas img)
        (getImageData 0 0 width height))))

(defmethod dimensions :default [_] nil)

(defmethod dimensions ::with-dims
  [img]
  {:width (.-width img)
   :height (.-height img)})

(defmethod put-data! :default [_ _] nil)

(defmethod put-data! ::image-like
  [canvas img]
  (.drawImage canvas img 0 0))

(defmethod put-data! js/ImageData
  [canvas data]
  (.putImageData canvas data 0 0))

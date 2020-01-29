(ns util.canvas
  "Procedures for doing stuff with HTML 2d canvas.")

(def pixel-stride
  "Byte length of RGBA canvas pixel" 4)

(defn make-ref
  "Create map containing canvas `elem` and its 2d drawing context."
  [^HTMLCanvasElement elem]
  {:elem elem
   :ctx (.getContext elem "2d")})

(defn get-data
  "Get RGBA data and scratch buffer from `canvas`."
  [canvas]
  (let [{:keys [elem ctx]} canvas
        width (.-width elem)
        height (.-height elem)
        data (.getImageData ctx 0 0 width height)
        scratch (.createImageData ctx width height)]
    {:data-seq (->> data (.-data) (array-seq) (partition pixel-stride))
     :scratch scratch
     :scratch-buffer (.-data scratch)}))

(defn copy-rgba!
  "Store RGBA groups from `byte-seq` into `dest` image data."
  [byte-seq ^Uint8ClampedArray dest]
  (let [bytes (-> byte-seq
                  (flatten)
                  (into-array))]
    (.set dest bytes 0)))

(defn transform-canvas!
  "Apply `transform` to RGBA `data` and draw result on `canvas`."
  [transform data canvas]
  (let [{:keys [ctx scratch data-seq scratch-buffer]} (merge canvas data)]
    (-> data-seq (transform) (copy-rgba! scratch-buffer))
    (.putImageData ctx scratch 0 0))
  nil)

(defn draw-image!
  "Draw image `img` into `canvas`."
  [^HTMLImageElement img canvas]
  (let [{:keys [elem ctx]} canvas
        width (.-width img)
        height (.-height img)]
    (doto elem
      (-> .-width (set! width))
      (-> .-height (set! height)))
    (.drawImage ctx img 0 0)
    canvas))

(defn open-image!
  "Open image at url `src` and draw it on `canvas`. Pass result RGBA data to `loaded` callback."
  [src loaded canvas]
  (doto (js/Image.)
    (-> .-crossOrigin (set! "Anonymous"))
    (-> .-src (set! src))
    (-> .-onload (set! #(-> %
                            (.-target)
                            (draw-image! canvas)
                            (get-data)
                            (loaded)))))
  nil)

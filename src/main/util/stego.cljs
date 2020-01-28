(ns util.stego
  "The fun file-hiding stuff."
  (:require [clojure.math.numeric-tower :as math]))

(def pattern-key
  "Mapping of color channel choice to flag bits in green channel."
  {:red   2r00
   :blue  2r01
   :split 2r11})

(defn euclid-distance
  "Euclidean distance between `v1` and `v2`."
  [v1 v2]
  (-> (map #(math/expt (- %1 %2) 2) v1 v2)
      (reduce +)
      (math/sqrt)))

(defn clear-insig
  "Clear out last 4 bits of `b`."
  [b]
  (bit-and b 2r11110000))

(defn splice-rb
  "Interleave byte `data` into `r` `g` `b` on the `:red` or `:blue` channels."
  [data [r g b]]
  (let [mask  (bit-and g 2r11111100)
        rmask (bit-or mask (pattern-key :red))
        bmask (bit-or mask (pattern-key :blue))]
    {:red  [data rmask b]
     :blue [r bmask data]}))

(defn splice-split
  "Interleave byte `data` into `r` `g` `b` on both the `:red` and `:blue` channels."
  [data [r g b]]
  (let [mask  (bit-and g 2r11111100)
        smask (bit-or mask (pattern-key :split))]
    {:split [(-> data (bit-shift-right 4) (bit-or (clear-insig r)))
             smask
             (-> data (bit-and 2r00001111) (bit-or (clear-insig b)))]}))




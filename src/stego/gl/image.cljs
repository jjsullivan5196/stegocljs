(ns stego.gl.image
  (:require [stego.gl.util :as gu]
            [stego.canvas.drawing :as dr]
            [iglu.core :refer [iglu->glsl]]))

(defprotocol ImageContext
  :extend-via-metadata true
  (draw-image [context props] "Draw some provided image onto the gl canvas."))

(def new-image-context
  (gu/wrap-commands
    (fn [rg]
      {`draw-image
       {:frag
        (iglu->glsl
          '{:precision "mediump float"
            :varyings {uv vec2}
            :uniforms {texture sampler2D}
            :signatures {main ([] void)}
            :functions
            {main
             ([] (= gl_FragColor (.brra (texture2D texture uv))))}})
        :vert
        (iglu->glsl
          '{:precision "mediump float"
            :varyings {uv vec2}
            :attributes {position vec2}
            :signatures {main ([] void)}
            :functions
            {main
             ([] (= uv position)
                 (= gl_Position (vec4 (- "1.0" (* "2.0" position)) 0 1)))}})
        :attributes
        {:position
         [-2 0
          0 -2
          2 2]}
        :uniforms
        {:texture #(rg.texture (:image %2))}
        :count 3}})))

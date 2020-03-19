(ns stego.gl.image
  (:require [play-cljc.gl.core :as glc]
            [play-cljc.gl.entities-2d :as e2d]
            [play-cljc.transforms :as transform]
            [play-cljc.macros-js :refer-macros [gl]]))

(defn image-setup
  [game {:keys [data width height] :as image}]
  (gl game disable (gl game CULL_FACE))
  (gl game disable (gl game DEPTH_TEST))
  (let [entity
        (-> (e2d/->image-entity game data width height)
            (assoc :clear {:color [1 1 1 1] :depth 1})
            (assoc-in
              [:fragment :functions 'main]
              '([] (= o_color (.rrga (texture u_image v_tex_coord))))))]
    (assoc game
           :entity (glc/compile game entity)
           :image image)))

(defn draw-image!
  [{:keys [image entity] :as game}]
  (let [{:keys [width height]} image]
    (glc/render
      game (-> entity
               (assoc :viewport {:x 0 :y 0 :width width :height height})
               (transform/project width height)
               (transform/translate 0 0)
               (transform/scale width height)))))

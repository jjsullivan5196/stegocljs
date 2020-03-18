(ns stego.gl.util
  (:require [stego.util :as u]
            ["regl" :as regl-init]))

(defn cmd-wrapper
  [cmd]
  (fn [_ & args]
    (tap> args)
    (apply cmd args)))

(defn wrap-commands
  "Return a function that constructs a regl context and registers `commands`. The
  function will return the context with the given commands as metadata."
  [commands]
  (fn [surface]
    (let [regl (regl-init surface)
          regl-commands (-> regl (commands) (u/update-vals (comp cmd-wrapper regl clj->js)))]
      (with-meta
        regl regl-commands))))

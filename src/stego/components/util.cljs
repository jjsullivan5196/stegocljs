(ns stego.components.util
  "Component utilities."
  (:require [helix.core :refer [defnc defhook $]]
            [helix.hooks :as hooks]
            [helix.dom :as d]
            [clojure.core.async :as a]))

(defhook use-thunk
  "Create a state variable which stores the result of some async thunk. The
  thunk will only be recomputed when any of `args` change."
  [create & args]
  (let [[value set-value] (hooks/use-state nil)]
    (hooks/use-effect args (a/take! (apply create args) set-value))
    value))

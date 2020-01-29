(ns util.core
  "Generic utilities.")

(defn use-ref!
  "Create a function that swaps in a component reference into atom `!at`. Optionally provide `mkref` to change the reference before storing it."
  ([!at] (use-ref! !at identity))
  ([mkref !at] (fn [el] (when el
                          (->> el (mkref) (reset! !at))))))

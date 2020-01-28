(ns util.core
  "Generic utilities.")

(defn use-ref!
  "Create a function that swaps in a component reference into atom `!at`, reference identity is assumed to be stable and will only be stored once. Optionally provide `mkref` to change the reference before storing it."
  ([!at] (use-ref! !at identity))
  ([!at mkref] (fn [el] (when (and el (not @!at))
                          (->> el (mkref) (reset! !at))))))

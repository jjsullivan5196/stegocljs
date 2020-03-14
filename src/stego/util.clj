(ns stego.util)

(defmacro extend-types
  "Pass `specs` to `extend-type` applied to `types`."
  [types & specs]
  `(doseq [t# ~types]
    (extend-type t# ~@specs)))

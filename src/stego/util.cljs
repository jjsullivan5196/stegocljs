(ns stego.util
  "Generic utilities."
  (:require [cljs-http.client :as http]
            [clojure.core.async :as a :refer [go <! >! chan promise-chan]])
  (:require-macros [stego.util]))

(defn update-vals
  "Apply function `f` to all values in map `m`. Return a new map with the updated
  values."
  [m f & args]
  (reduce-kv #(assoc %1 %2 (apply f %3 args)) {} m))

(defn fetch-blob!
  "Get some `url` as a blob object."
  [url]
  (http/get
    url {:response-type :blob
         :with-credentials? false
         :channel (promise-chan (map :body))}))

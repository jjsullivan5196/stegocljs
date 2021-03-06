(ns stego.util
  "Generic utilities."
  (:require [cljs-http.client :as http]
            [clojure.core.async :as a :refer [go <! >! chan promise-chan]])
  (:require-macros [stego.util]))

(defn fetch-blob!
  "Get some `url` as a blob object."
  [url]
  (http/get
    url {:response-type :blob
         :with-credentials? false
         :channel (promise-chan (map :body))}))

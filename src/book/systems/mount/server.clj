(ns book.systems.mount.server
  (:require
   [book.systems.mount.config :refer [config]]
   [mount.core :as mount :refer [defstate]]
   [ring.adapter.jetty :refer [run-jetty]]))

(def app (constantly {:status 200 :body "Hello"}))

(defstate server
  :start
  (let [{jetty-opt :jetty} config]
    (run-jetty app jetty-opt))
  :stop
  (.stop server))

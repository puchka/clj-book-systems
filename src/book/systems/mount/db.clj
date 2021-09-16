(ns book.systems.mount.db
  (:require
   [mount.core :as mount :refer [defstate]]
   [hikari-cp.core :as cp]
   [book.systems.mount.config :refer [config]]))

(defstate db
  :start
  (let [{pool-opt :pool} config
        store (cp/make-datasource pool-opt)]
    {:datasource store})
  :stop
  (-> db :datasource cp/close-datasource))

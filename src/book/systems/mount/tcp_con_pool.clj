(ns book.systems.mount.tcp-con-pool
  (:require [mount.core :refer [defstate]]
            [clj-http.conn-mgr :refer [make-reusable-conn-manager
                                       shutdown-manager]]
            [book.systems.mount.config :refer [config]]))

(defstate tcp-con-pool
  :start
  (let [{http-opt :http} config]
    (make-reusable-conn-manager http-opt))
  :stop
  (shutdown-manager tcp-con-pool))

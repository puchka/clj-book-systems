(ns book.systems.mount.config
  (:require
   [clojure.edn :as edn]
   [mount.core :as mount :refer [defstate]]))

(defstate config
  :start
  (-> "system.config.edn" slurp edn/read-string))

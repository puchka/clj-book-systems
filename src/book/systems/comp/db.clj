(ns book.systems.comp.db
  (:require
   [com.stuartsierra.component :as component]
   [hikari-cp.core :as cp]
   [clojure.pprint :as pprint]
   [clojure.java.jdbc :as jdbc]))

(defprotocol IDB
  (query [this sql-params])
  (update! [this table set-map where-clause]))

(defrecord DB [options db-spec]
  component/Lifecycle
  (start [this]
    (let [pool (cp/make-datasource options)]
      (assoc this :db-spec {:datasource pool})))
  (stop [this]
    (-> db-spec :datasource cp/close-datasource)
    (assoc this :db-spec nil))
  IDB
  (query [this sql-params]
    (jdbc/query db-spec sql-params))

  (update! [this table set-map where-clause]
    (jdbc/update! db-spec table set-map where-clause)))

(defn make-db [options]
  (map->DB {:options options}))

(comment
  (def options {:minimum-idle       10
                :maximum-pool-size  10
                :adapter            "postgresql"
                :username           "book"
                :password           "book"
                :database-name      "book"
                :server-name        "127.0.0.1"
                :port-number        5432})

  (def db-created (make-db options))

  (def db-started (component/start db-created))

  (query db-started "select * from users")
  (update! db-started :users {:name "Ivan"} ["id = ?" 42])

  (def db-stopped (component/stop db-started)))

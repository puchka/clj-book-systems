(ns book.systems.mount.worker
  (:require
   [mount.core :as mount :refer [defstate]]
   [clojure.java.jdbc :as jdbc]
   [clj-postgresql.types :as types]
   [clj-http.client :as client]
   [clojure.tools.logging :as log]
   [clojure.data.json :as json]
   [book.systems.mount.db :refer [db]]
   [book.systems.mount.config :refer [config]]
   [book.systems.mount.tcp-con-pool :refer [tcp-con-pool]]))

(def query
  "SELECT * FROM requests WHERE NOT is_processed
   LIMIT 1 FOR UPDATE;")

(defn get-ip-info
  [ip]
  (:body (client/post "https://iplocation.com"
                      {:form-params {:ip ip}
                       :accept :json
                       :connection-manager tcp-con-pool})))

(defn task-fn
  []
  (jdbc/with-db-transaction [tx db]
    (when-let [request (first (jdbc/query tx query))]
      (let [{:keys [id ip]} request
            info (json/read-str (get-ip-info ip) :key-fn keyword)
            fields {:is_processed true
                    :zip (:postal_code info)
                    :country (:country_name info)
                    :city (:city info)
                    :lat (:lat info)
                    :lon (:lng info)}]
        (jdbc/update! tx :requests
                      fields
                      ["id = ?" id])))))

(defn make-task
  [flag opt]
  (let [{:keys [sleep]} opt]
    (future
      (while @flag
        (try
          (task-fn)
          (catch Throwable e
            (log/error e))
          (finally
            (Thread/sleep sleep)))))))

(defstate worker
  :start
  (let [{task-opt :worker} config
        flag (atom true)
        task (make-task flag task-opt)]
    {:flag flag :task task})
  :stop
  (let [{:keys [flag task]} worker]
    (reset! flag false)
    (while (not (realized? task))
      (log/info "Waiting for the task to complete")
      (Thread/sleep 300))))

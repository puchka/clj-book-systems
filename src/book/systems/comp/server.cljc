(ns book.systems.comp.server
  (:require
   [com.stuartsierra.component :as component]
   [ring.adapter.jetty :refer [run-jetty]]))

(def app (constantly {:status 200 :body "Hello fom component"}))

(defrecord Server [options server]
  component/Lifecycle
  (start [this]
    (let [server (run-jetty app options)]
      (assoc this :server server)))

  (stop [this]
    (.stop server)
    (assoc this :server nil)))

(comment
  (defrecord BadServer [options server]
    component/Lifecycle
    (start [this]
      {:server (run-jetty app options)})
    (stop [this]
      (.stop server)
      nil))

  (def bs-created (map->BadServer
                   {:options {:port 8080 :join? false}}))

  (def bs-started (component/start bs-created)))

(extend-protocol component/Lifecycle
  #?(:clj java.lang.Object :cljs default)
  (start [this]
    this)
  (stop [this]
    this))

(comment
  (defn make-server
    [options]
    (map->Server {:options options}))

  (def s-created
    (make-server {:port 8080 :join? false}))

  (def s-started (component/start s-created))

  (def s-stopped (component/stop s-started)))

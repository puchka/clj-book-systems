(ns book.systems.mount.core
  (:require
   [mount.core :as mount]
   book.systems.mount.config
   book.systems.mount.db
   book.systems.mount.server
   book.systems.mount.worker))

(defn start []
  (mount/start))

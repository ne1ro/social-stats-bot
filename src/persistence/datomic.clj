(ns persistence.datomic
  "Datomic DB adapter for persistence port"
  (:require [integrant.core :as ig]
            [social-stats-bot.persistence :refer [Persistence]]))

(defrecord Datomic
    [conn]
  Persistence

  (get-user [conn nickname provider])

  (insert-user [conn user-params])

  (list-stats [this nickname provider stats-params]))

(defmethod ig/init-key :datomic [_ {:keys [db]}] (->Datomic db))

(defmethod ig/halt-key! :datomic [_ _] nil)

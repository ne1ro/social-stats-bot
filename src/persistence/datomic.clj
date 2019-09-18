(ns persistence.datomic
  "Datomic DB adapter for persistence port"
  (:require [integrant.core :as ig]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as stest]
            [clojure.string :as st]
            [datomic.client.api :as d]
            [social-stats-bot.persistence :refer [Persistence]]))

(s/def ::endpoint string?)
(s/def ::access-keys string?)
(s/def ::db-name string?)
(s/def ::conf (s/keys :req [::endpoint ::access-keys ::db-name]))

(def schema
  [{:db/ident :user/provider
    :db/valueType :db.type/keyword
    :db/cardinality :db.cardinality/one}

   {:db/ident :user/first-name
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}

   {:db/ident :user/last-name
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}

   {:db/ident :user/nickname
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}

   {:db/ident :user/posts-count
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one}

   {:db/ident :user/followers-count
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one}

   {:db/ident :user/followings-count
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one}

   {:db/ident :user/avatar
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}

   {:db/ident :user/last-fetched-at
    :db/valueType :db.type/instant
    :db/cardinality :db.cardinality/one}])

(def user-query '[:find  (pull ?e [*])
                  :where [?nickname :user/nickname ?provider :user/provider]])

(defrecord Datomic
           [conn]
  Persistence

  (get-user [{:keys [conn]} nickname provider]
    (d/q user-query (d/db conn) nickname provider))

  (insert-user [{:keys [conn]} user-params]
    (d/transact conn {:tx-data [user-params]}))

  (list-stats [{:keys [conn]} nickname provider stats-params]))

(defmethod ig/pre-init-spec ::datomic [_] ::conf)

(defmethod ig/init-key :datomic
  [_ {:keys [::access-keys ::endpoint ::db-name] :as db}]
  (let [[access-key secret] (st/split access-keys #",")]
    (-> {:access-key access-key
         :secret secret
         :server-type :peer-server
         :validate-hostnames false
         :endpoint endpoint}
        d/client
        (d/connect {:db-name db-name})
        (d/transact {:tx-data schema})
        ->Datomic)))

(defmethod ig/halt-key! :datomic [_ _conn] nil)

(stest/instrument)

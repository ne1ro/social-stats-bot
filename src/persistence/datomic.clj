(ns persistence.datomic
  "Datomic DB adapter for persistence port"
  (:require [integrant.core :as ig]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as stest]
            [clojure.string :as st]
            [datomic.api :as d]
            [social-stats-bot.persistence :refer [Persistence]]))

(s/def ::endpoint string?)
(s/def ::db-name string?)
(s/def ::conf (s/keys :req [::endpoint ::db-name]))

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

(def user-query '[:find  ?user
                  :where [?user :user/nickname ?nickname]
                  [?user :user/provider ?provider]])

(defn- ->db-user [user]
  (let [ks (keys user)
        renamed-ks (map #(keyword "user" (name %)) ks)]
    (clojure.set/rename-keys user (zipmap ks renamed-ks))))

(defn- db-conn [endpoint db-name]
  (when (= endpoint "datomic:mem://")
    (let [addr (str endpoint db-name)]
      (d/create-database addr)
      (d/connect addr))))

(defrecord Datomic
           [conn]
  Persistence

  (get-user [{:keys [conn]} nickname provider]
    (let [res (d/q user-query (d/db conn) [nickname provider])]
      (prn res) (first res)))

  (insert-user [{:keys [conn]} user-params]
    (when (d/transact conn [{:tx-data [(->db-user user-params)]}])
      user-params))

  (list-stats [{:keys [conn]} nickname provider stats-params]))

(defmethod ig/pre-init-spec :datomic [_] ::conf)

(defmethod ig/init-key :datomic
  [_ {:keys [::endpoint ::db-name] :as db}]
  (let [conn (db-conn endpoint db-name)]
    @(d/transact conn schema)
    (->Datomic conn)))

(defmethod ig/halt-key! :datomic [_ _conn] nil)

(stest/instrument)

(ns persistence.datomic
  "Datomic DB adapter for persistence port"
  (:require [integrant.core :as ig]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as stest]
            [clojure.string :as st]
            [datomic.client.api :as d]
            ;; [datomic.api :as dapi]
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
                  :where [?e :user/nickname ?nickname]
                  [?e :user/provider ?provider]])

(defn- ->db-user [user]
  (let [ks (keys user)
        renamed-ks (map #(keyword "user" (name %)) ks)]
    (clojure.set/rename-keys user (zipmap ks renamed-ks))))

(defn- db-conn [endpoint access-keys db-name]
  ;; TODO: handle in-memory connections by multimethods
  (when (= endpoint "datomic:mem://")
    (do (dapi/create-database (str endpoint db-name)))
    (let [[access-key secret] (st/split access-keys #",")
          conf (d/client {:access-key access-key
                          :secret secret
                          :server-type :peer-server
                          :validate-hostnames false
                          :endpoint endpoint})]
      (d/connect conf {:db-name db-name}))))

(defrecord Datomic
           [conn]
  Persistence

  (get-user [{:keys [conn]} nickname provider]
    (let [res (d/q user-query (d/db conn) nickname provider)]
      (prn res) (first res)))

  (insert-user [{:keys [conn]} user-params]
    (when (d/transact conn {:tx-data [(->db-user user-params)]})
      user-params))

  (list-stats [{:keys [conn]} nickname provider stats-params]))

(defmethod ig/pre-init-spec :datomic [_] ::conf)

(defmethod ig/init-key :datomic
  [_ {:keys [::access-keys ::endpoint ::db-name] :as db}]
  (let [conn (db-conn endpoint access-keys db-name)]
    (prn "CONNь" conn)
    (d/transact conn {:tx-data schema}) (->Datomic conn)))

(defmethod ig/halt-key! :datomic [_ _conn] nil)

(stest/instrument)

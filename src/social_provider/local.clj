(ns social-provider.local
  (:import (com.github.javafaker Faker))
  (:require [social-stats-bot.social-provider :refer [SocialProvider]]
            [integrant.core :as ig]))

(def f (new Faker))

(defn- generate-user []
  {:first-name (-> f .hipster .word)
   :last-name (-> f .pokemon .name)
   :posts-count (rand-int 10)
   :followers-count (rand-int 10)
   :followings-count (rand-int 10)
   :avatar (-> f .avatar .image)
   :last-fetched-at nil})

(defrecord ProviderLocal
           [conf]
  SocialProvider

  (fetch-user [_conf nickname]
    (assoc (generate-user) :nickname nickname :provider "instagram")))

(defmethod ig/init-key :provider-local [_ conf] (->ProviderLocal conf))
(defmethod ig/halt-key! :provider-local [_ _] nil)

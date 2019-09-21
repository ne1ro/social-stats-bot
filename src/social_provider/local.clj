(ns social-provider.local
  (:require [social-stats-bot.social-provider :refer [SocialProvider]]
            [social-stats-bot :refer [build-user]]
            [integrant.core :as ig]))

(defrecord ProviderLocal
           [conf]
  SocialProvider

  (fetch-user [_conf nickname]
    (build-user :nickname nickname :provider "instagram")))

(defmethod ig/init-key :provider-local [_ conf] (->ProviderLocal conf))
(defmethod ig/halt-key! :provider-local [_ _] nil)

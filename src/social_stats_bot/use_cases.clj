(ns social-stats-bot.use-cases
  "Use cases of social stats bot"
  (:require [social-stats-bot.domain :as domain]
            [integrant.core :as ig]))

;; Procotol for social stats use cases
(defprotocol SocialStatsBot
  "Social stats bot use cases"

  (get-user [this nickname provider]
    "Gets a user by her or his nickname and social acc provider")

  (get-stats [this nickname provider]
    "Gets stats for a social account by user nickname and provider"))

;; Implementation of social stats use cases
(defrecord SocialStatsUseCases
  [deps]
  SocialStatsBot

  (get-user
    [{:keys [db social-provider]} nickname provider])

  (get-stats
    [{:keys [db graph]} nickname provider]))

;; Configure use cases on startup
(defmethod ig/init-key :use-cases
  [_ {:keys [db social-provider graph] :as dependencies}]
  (->SocialStatsUseCases dependencies))

(defmethod ig/halt-key! :use-cases [_ _] nil)

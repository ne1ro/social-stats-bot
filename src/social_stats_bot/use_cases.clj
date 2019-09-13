(ns social-stats-bot.use-cases
  "Use cases of social stats bot"
  (:require [social-stats-bot.domain :as domain]
            [social-stats-bot.persistence :as p]
            [social-stats-bot.graph :as g]
            [social-stats-bot.social-provider :as sp]
            [integrant.core :as ig]))

(defn- fetch-and-insert-user [db social-provider nickname]
  (some->> nickname (sp/fetch-user social-provider) (p/insert-user db)))

(defn- draw-graph [db nickname provider graph stats-params user]
  (some-> db
          (p/list-stats nickname provider stats-params)
          #(g/draw-user-stats graph user % stats-params)))

;; Procotol for social stats use cases
(defprotocol SocialStatsBot
  "Social stats bot use cases"

  (get-user [this nickname provider]
    "Gets a user by her or his nickname and social acc provider")

  (list-stats [this nickname provider stats-params]
    "Gets stats for a social account by user nickname and provider"))

;; Implementation of social stats use cases
(defrecord SocialStatsUseCases
    [deps]
  SocialStatsBot

  (get-user
    [{:keys [db social-provider]} nickname provider]
    (if-let [user (p/get-user db nickname provider)]
      user
      (fetch-and-insert-user db social-provider nickname)))

  (list-stats
    [{:keys [db graph social-provider]} nickname provider stats-params]
    (if-let [user (p/get-user db nickname provider)]
      {:user user
       :stats-params stats-params
       :graph (draw-graph db nickname provider graph stats-params user)}
      (when-let [new-user (fetch-and-insert-user db social-provider nickname)]
        {:user new-user
         :stats-params stats-params
         :graph (draw-graph db nickname provider graph stats-params new-user)}))))

;; Configure use cases on startup
(defmethod ig/init-key :use-cases
  [_ {:keys [db social-provider graph] :as dependencies}]
  (->SocialStatsUseCases dependencies))

(defmethod ig/halt-key! :use-cases [_ _] nil)

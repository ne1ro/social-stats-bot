(ns web.service
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as st]
            [clojure.string :as str]
            [compojure.core :refer :all]
            [morse.handlers :as h]
            [social-stats-bot.messenger
             :refer [send-message send-photo]]
            [social-stats-bot.use-cases :refer [SocialStatsBot]]))

(def default-provider "instagram")

(s/def ::messenger map?)
(s/def ::use-cases map?)

(defn- show-user-info
  [{:keys [posts-count followers-count followings-count
           first-name last-name nickname]}]
  (let [lines [(str first-name " " last-name)
               (str "@" nickname)
               "/n"
               (str "Posts count: " posts-count)
               (str "Followers count: " followers-count)
               (str "Followings count: " followings-count)]]
    (str/join "\n" lines)))

(defn- get-account [use-cases messenger {{chat-id :id} :chat command :text}]
  (let [[_ username] (str/split command #" ")
        {avatar :avatar :as u} (.get-user use-cases username default-provider)]
    (send-photo messenger avatar chat-id)
    (send-message messenger (show-user-info u) chat-id)))

(defn- start [messenger {{id :id :as chat} :chat
                         {first-name :first_name} :from}]
  (println "Bot joined new chat: " chat)
  (send-message messenger
                (str "Welcome, "
                     first-name
                     " ! Type /help to see more details.") id))

(defn- help [messenger {{id :id :as chat} :chat}]
  (println "Help was requested in " chat)
  (send-message messenger
                "Type `/get_user account`
    to get user's current info and `/get_stats account daily|weekly|monthly`"
                id))

(s/fdef get-bot :args
  (s/cat :messenger ::messenger :use-cases ::use-cases))
(defn get-bot [messenger use-cases]
  (h/defhandler bot-handler
    (h/command-fn "start" (partial start messenger))
    (h/command-fn "get_user" (partial get-account use-cases messenger))
    (h/command-fn "help" (partial help messenger))
    (h/message message (println "Intercepted message:" message))))

(st/instrument)

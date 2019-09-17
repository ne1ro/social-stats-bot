(ns web.service
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as st]
            [clojure.string :as str]
            [clj-http.client :as http]
            [compojure.core :refer :all]
            [morse.handlers :as h]
            [morse.api :as t]
            [social-stats-bot.use-cases :refer [SocialStatsBot]]
            [ring.util.response :as ring-resp]))

(def default-provider "instagram")

(s/def ::telegram-token string?)
(s/def ::endpoint string?)
(s/def ::use-cases map?)

(defn- show-user-info
  [{:keys [posts-count followers-count followings-count
           first-name last-name nickname]}]
  (let [lines [(str first-name " " last-name)
               (str "@" nickname)
               (str "=============")
               (str "Posts count: " posts-count)
               (str "Followers count: " followers-count)
               (str "Followings count: " followings-count)]]
    (str/join "\n" lines)))

(defn- get-account [use-cases token {{chat-id :id} :chat command :text}]
  (let [[_ username] (str/split command #" ")
        {avatar :avatar :as u} (.get-user use-cases username default-provider)]
    (t/send-photo token chat-id avatar)
    (t/send-text token chat-id (show-user-info u))))

(defn- start [token {{id :id :as chat} :chat
                     {first-name :first_name} :from}]
  (println "Bot joined new chat: " chat)
  (t/send-text token
               id (str "Welcome, "
                       first-name
                       " ! Type /help to see more details.")))

(defn- help [token {{id :id :as chat} :chat}]
  (println "Help was requested in " chat)
  (t/send-text token id "Type `/get_user account`
    to get user's current info and `/get_stats account daily|weekly|monthly`"))

(s/fdef get-bot :args
  (s/cat :token ::telegram-token :endpoint ::endpoint :use-cases ::use-cases))
(defn get-bot [token endpoint use-cases]
  (-> t/base-url (str token "/deleteWebhook") http/get)
  (t/set-webhook token endpoint)

  (h/defhandler bot-handler
    (h/command-fn "start" (partial start token))
    (h/command-fn "get_user" (partial get-account use-cases token))
    (h/command-fn "help" (partial help token))
    (h/message message (println "Intercepted message:" message))))

(st/instrument)

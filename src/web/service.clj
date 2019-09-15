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

(defn- get-account [use-cases token {{chat-id :id} :chat command :text}]
  (let [[_ username] (str/split command #" ")
        {avatar :avatar :as u} (.get-user use-cases username default-provider)]
    (t/send-photo token chat-id avatar)
    (t/send-text token chat-id (str u))))

(s/fdef get-bot :args
  (s/cat :token ::telegram-token :endpoint ::endpoint :use-cases ::use-cases))
(defn get-bot [token endpoint use-cases]
  (-> t/base-url (str token "/deleteWebhook") http/get)
  (t/set-webhook token endpoint)

  (h/defhandler bot-handler
    (h/command-fn "start" (fn [{{id :id :as chat} :chat
                               {first-name :first_name} :from}]
                            (println "Bot joined new chat: " chat)
                            (t/send-text token
                                         id (str "Welcome, "
                                                 first-name
                                                 " ! Type /help to see more details."))))

    (h/command-fn "get-user" (partial get-account use-cases token))

    (h/command "help"
               {{id :id :as chat} :chat}
               (println "Help was requested in " chat)
               (t/send-text token id "Type `/get_user account`
    to get user's current info and `/get_stats account daily|weekly|monthly`"))

    (h/message message (println "Intercepted message:" message))))

(st/instrument)

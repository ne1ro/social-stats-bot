(ns web.service
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as st]
            [compojure.core :refer :all]
            [morse.handlers :as h]
            [morse.api :as t]
            [social-stats-bot.use-cases :refer [SocialStatsBot]]
            [ring.util.response :as ring-resp]))

(defn get-bot [token endpoint use-cases]
  (prn token endpoint)
  (t/set-webhook token endpoint)

  (h/defhandler bot-handler
    (h/command-fn "start" (fn [{{id :id :as chat} :chat}]
                            (println "Bot joined new chat: " chat)
                            (t/send-text token id "Welcome!")))

    (h/command "get-user" {{id :id} :chat :as params}
               (println params)
               (t/send-text (str params)))

    (h/command "help"
               {{id :id :as chat} :chat}
               (println "Help was requested in " chat)
               (t/send-text token id "Help is on the way"))

    (h/message message (println "Intercepted message:" message))))

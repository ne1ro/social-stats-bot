(ns web.service
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as st]
            [compojure.core :refer :all]
            [morse.handlers :as h]
            [morse.api :as t]
            [ring.util.response :as ring-resp]))

(h/defhandler bot-handler
  (h/command "help"
             {{id :id :as chat} :chat}
             (println "Help was requested in " chat)
             (t/send-text (System/getenv "TELEGRAM_TOKEN") id "Help is on the way")))

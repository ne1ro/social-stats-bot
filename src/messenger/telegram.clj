(ns messenger.telegram
  "Telegram messenger adapter"
  (:require [social-stats-bot.messenger :refer [Messenger]]
            [clj-http.client :as http]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as st]
            [integrant.core :as ig]
            [morse.api :as t]))

(s/def ::non-empty-str (complement clojure.string/blank?))
(s/def ::endpoint ::non-empty-str)
(s/def ::token ::non-empty-str)
(s/def ::conf (s/keys :req [::endpoint ::token]))

(defrecord Telegram
           [conf]
  Messenger

  (set-endpoint [{{token ::token endpoint ::endpoint} :conf}]
    (-> t/base-url (str token "/deleteWebhook") http/get)
    (t/set-webhook token endpoint))

  (send-message [{{token ::token} :conf} text chat-id]
    (t/send-text token chat-id text))

  (send-photo [{{token ::token} :conf} photo chat-id]
    (t/send-photo token chat-id photo)))

(defmethod ig/pre-init-spec :telegram [_] ::conf)

(defmethod ig/init-key :telegram [_ conf]
  (let [tg (->Telegram conf)] (.set-endpoint tg) tg))

(defmethod ig/halt-key! :telegram [_ _] nil)

(st/instrument)

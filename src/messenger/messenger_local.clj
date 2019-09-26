(ns messenger.messenger-local
  "Test adapter for the messenger port"
  (:require [social-stats-bot.messenger :refer [Messenger]]
            [integrant.core :as ig]))

(defrecord MessengerLocal
           [conf]
  Messenger

  (set-endpoint [_conf] nil)
  (send-message [_conf m _chat-id] m)
  (send-photo [_conf photo _chat-id] photo))

(defmethod ig/init-key :messenger-local [_ conf] (->MessengerLocal conf))
(defmethod ig/halt-key! :messenger-local [_ conf] nil)

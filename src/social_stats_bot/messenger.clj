(ns social-stats-bot.messenger
  "Messenger port")

(defprotocol Messenger
  "Messenger bot actions behaviour"

  (set-endpoint [conf] "Sets an endpoint for the webhook")
  (send-message [conf text chat-id] "Sends a message")
  (send-photo [conf file chat-id] "Sends a photo"))

(ns social-stats-bot.messenger
  "Messenger port")

(defprotocol Messenger
  "Messenger bot actions behaviour"

  (command [this cmd params] "Parses a messenger command")
  (send-message [this payload type] "Sends either a message or a photo"))

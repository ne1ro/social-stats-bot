(ns social-stats-bot.current-date
  "Current date port")

(defprotocol CurrentDate
  "Current date behaviour"

  (get-date [conf] "Returns current date"))

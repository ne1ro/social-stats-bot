(ns social-stats-bot.persistence
  "Persistence port for social stats bot")

(defprotocol Persistence
  "Stats bot persistence operations"

  (get-user [this nickname provider] "Gets an user by nickname and provider")

  (insert-user [this user-params] "Inserts an user")

  (list-stats
    [this nickname provider stats-params]
    "Returns user stats by params"))

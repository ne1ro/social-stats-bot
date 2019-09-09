(ns social-stats-bot.social-provider
  "Social provider port")

(defprotocol SocialProvider
  "Social provider actions"
  (fetch-user [this nickname] "Fetches user data"))

(ns social-stats-bot.graph
  "Port for drawing graphs")

(defprotocol Graph
  "Draws graphs"
  (draw-user-stats [this user stats params] "Draws users stats"))

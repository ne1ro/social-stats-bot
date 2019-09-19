(ns user
  (:require [integrant.repl :refer [clear go halt prep init reset reset-all]]
            [social-stats-bot.core :as core]))

(integrant.repl/set-prep! (-> "dev" core/config constantly))

(prep)

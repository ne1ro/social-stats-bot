(ns current-date.current-date
  "Current date adapter"

  (:require [social-stats-bot.current-date :refer [CurrentDate]]))

(defrecord NewDate [conf]
  CurrentDate

  (get-date [_conf] (java.util.Date.)))

(defmethod ig/init-key :current-date [_ config]
  (->NewDate config))

(defmethod ig/halt-key! :telegram [_ _] nil)

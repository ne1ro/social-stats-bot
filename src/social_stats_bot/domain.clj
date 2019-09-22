(ns social-stats-bot.domain
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as st]
            [clojure.set :refer [rename-keys]]
            [expound.alpha :as expound]))

;; User attributes
(s/def ::pos-int (s/and #(>= % 0) int?))
(s/def ::provider #{:instagram})
(s/def ::followings-count ::pos-int)
(s/def ::posts-count ::pos-int)
(s/def ::followers-count ::pos-int)
(s/def ::avatar string?)
(s/def ::first-name string?)
(s/def ::last-name string?)
(s/def ::nickname string?)
(s/def ::last-fetched-at inst?)

(s/def ::user
  (s/keys :req-un [::first-name ::provider ::nickname]
          :opt-un [::last-name ::avatar ::posts-count ::followers-count
                   ::followings-count ::last-fetched-at]))

;; Stats attributes
(s/def ::start-date inst?)
(s/def ::end-date inst?)
(s/def ::split-by #{:month :day :week})
(s/def ::graph string?)

(s/def ::stats-params (s/keys :req-un [::start-date ::end-date ::split-by]
                              :opt-un []))
(s/def ::stats (s/keys :req-un [::user ::graph ::stats-params] :opt-un []))

(st/instrument)

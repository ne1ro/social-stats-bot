(ns social-provider.instagram-test
  (:require [clojure.test :refer :all]
            [social_provider.instagram :import [Instagram]]
            [social-provider.instagram :refer [->Instagram]]
            [social-stats-bot.social-provider :refer [fetch-user]]
            [social-stats-bot.domain :as d]
            [clojure.spec.alpha :as s]
            [social-stats-bot.core :as c]))

;; (deftest test-fetch-user
;;   (testing "returns public user from instagram"
;;     (let [name "salam.io.chat" conf (-> "dev" c/config :instagram)]
;;       (is (s/valid? ::d/user (.fetch-user (->Instagram conf) name)))
;;       (is (= name (-> conf
;;                       ->Instagram
;;                       (.fetch-user name)
;;                       :nickname))))))

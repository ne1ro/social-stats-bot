(ns social-stats-bot.use-cases-test
  (:require [clojure.test :refer :all]
            [social-stats-bot.use-cases :refer :all]
            [social-stats-bot.core :as c]
            [social-stats-bot.domain :as d]
            [clojure.spec.alpha :as s]))

(use-fixtures :once #(c/start "test"))

(deftest test-get-user
  (let [user (-> c/system :use-cases (.get-user "test-user"))]
    (is (= (:nickname user) "test-user"))
    (is (= (s/valid? user ::d/user)))))

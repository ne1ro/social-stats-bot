(ns social-stats-bot.use-cases-test
  (:require [clojure.test :refer :all]
            [social-stats-bot.use-cases :refer :all]
            [social-stats-bot.core :as c]
            [social-stats-bot.domain :as d]
            [integrant.core :as ig]
            [clojure.spec.alpha :as s]))

(def ^:dynamic *system* nil)

(defn- setup-system [f]
  (let [conf (-> "test" c/config (dissoc :web))]
    (alter-var-root #'*system* (-> conf ig/init constantly))
    (f)
    (alter-var-root #'*system* ig/halt!)))

(use-fixtures :once setup-system)

(deftest test-get-user
  (let [user (-> *system* :use-cases (get-user "test-user" "instagram"))]
    (is (= (:nickname user) "test-user"))
    (is (= (s/valid? user ::d/user)))))

(ns social-stats-bot.use-cases-test
  (:require [clojure.test :refer :all]
            [social-stats-bot.use-cases :refer :all]
            [social-stats-bot.core :as c]
            [social-stats-bot.domain :as d]
            [integrant.core :as ig]
            [datomic.api :as dat]
            [clojure.spec.alpha :as s]))

(def ^:dynamic *system* nil)

(defn- setup-system [f]
  (let [{{endpoint :persistence.datomic/endpoint
          db-name :persistence.datomic/db-name} :datomic :as conf}
        (-> "test" c/config (dissoc :web))
        db-url (str endpoint db-name)]
    (dat/delete-database db-url)
    (alter-var-root #'*system* (-> conf ig/init constantly))
    (f)
    (dat/delete-database db-url)
    (alter-var-root #'*system* ig/halt!)))

(use-fixtures :once setup-system)

(deftest test-get-user
  (testing "fetches and insert user if it hasn't been saved before"
    (let [user (-> *system* :use-cases (get-user "test-user" "instagram"))]
      (is (= (:nickname user) "test-user"))
      (is (= (:provider user) :instagram))
      (is (= (s/valid? user ::d/user))))))

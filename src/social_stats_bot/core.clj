(ns social-stats-bot.core
  "Entry point to the application"
  (:require [integrant.core :as ig]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as st]
            [social-stats-bot.use-cases]
            [persistence.datomic]))

(s/def ::env #{"dev" "test" "prod"})

(def ^:private system (atom nil))
(def ^:private alter-system (partial alter-var-root #'system))

(s/fdef config :args (s/cat :env ::env) :ret map?)
(defn config [env]
  (some-> env (str "/config.edn") clojure.java.io/resource slurp ig/read-string))

(s/fdef start :args (s/cat :env ::env) :ret map?)
(defn start
  "Starts a system"
  [env]
  (-> env config ig/init constantly alter-system))

(defn stop
  "Stops a system"
  []
  (alter-system ig/halt!))

(defn -main
  "The entry-point for 'lein run'"
  [& args]
  (println "\nRunning a system...")
  (let [env (or (System/getenv "ENV") "dev")]
    (start env)))

(-main)
(st/instrument `config `start)

;; (with-handler :term
;;   (log/info "Caught SIGTERM, quitting")
;;   (system-stop)
;;   (log/info "All components shut down")
;;   (exit))

(ns social-stats-bot.core
  "Entry point to the application"
  (:gen-class) ; for -main method in uberjar
  (:require [integrant.core :as ig]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as st]
            [messenger.messenger-local]
            [messenger.telegram]
            [persistence.datomic]
            [social-provider.instagram]
            [social-provider.provider-local]
            [social-stats-bot.use-cases]
            [web.server]))

(s/def ::env #{"dev" "test" "prod"})

(defonce system nil)
(def alter-system ^:private (partial alter-var-root #'system))

(defn tag-env
  "Implements #env extension for EDN files"
  [vr]
  (cond
    (symbol? vr) (System/getenv (name vr))
    (string? vr) (System/getenv vr)
    :else (throw (new Exception "Wrong var type!"))))

(s/fdef config :args (s/cat :env ::env) :ret map?)
(defn config [env]
  (some->> "/config.edn"
           (str env)
           clojure.java.io/resource
           slurp
           (ig/read-string {:readers {'env tag-env}})))

(s/fdef start :args (s/cat :env ::env) :ret map?)
(defn start
  "Starts a system"
  [env]
  (let [conf (config env)]
    (alter-system (constantly (ig/init conf)))))

(defn stop
  "Stops a system"
  []
  (println "\nStopping a system...")
  (alter-system ig/halt!))

(defn -main
  "The entry-point for 'lein run'"
  [& args]
  (println "\nRunning a system...")
  (let [env (or (System/getenv "ENV") "dev")] (start env)))

(st/instrument)

;; (with-handler :term
;;   (log/info "Caught SIGTERM, quitting")
;;   (system-stop)
;;   (log/info "All components shut down")
;;   (exit))

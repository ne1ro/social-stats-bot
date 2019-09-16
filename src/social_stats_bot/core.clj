(ns social-stats-bot.core
  "Entry point to the application"
  (:gen-class) ; for -main method in uberjar
  (:require [integrant.core :as ig]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as st]
            [social-stats-bot.use-cases]
            [social-provider.instagram]
            [web.server]
            [persistence.datomic]))

(s/def ::env #{"dev" "test" "prod"})

(defonce system (atom nil))

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
  (->> env config ig/init constantly (alter-var-root #'system)))

(defn stop
  "Stops a system"
  []
  (println "\nStopping a system...")
  (ig/halt! @system))

(defn -main
  "The entry-point for 'lein run'"
  [& args]
  (println "\nRunning a system...")
  (let [env (or (System/getenv "ENV") "dev")] (start env)))

;; (-main)

(st/instrument `config `start)

;; (with-handler :term
;;   (log/info "Caught SIGTERM, quitting")
;;   (system-stop)
;;   (log/info "All components shut down")
;;   (exit))

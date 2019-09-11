(ns social-stats-bot.core
  "Entry point to the application"
  (:require [integrant.core :as ig]
            [social-stats-bot.use-cases]
            [persistence.datomic]))

(def ^:private system (atom nil))

(def ^:private alter-system (partial alter-var-root #'system))

(defn config [env]
  (some-> env (str "/config.edn") clojure.java.io/resource slurp ig/read-string))

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

;; (with-handler :term
;;   (log/info "Caught SIGTERM, quitting")
;;   (system-stop)
;;   (log/info "All components shut down")
;;   (exit))

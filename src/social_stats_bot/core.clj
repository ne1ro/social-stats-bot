(ns social-stats-bot.core
  "Entry point to the application"
  (:require [integrant.core :as ig]))

(defn config [env]
  (-> env (str "/config.edn") clojure.java.io/resource slurp ig/read-string))

(defn start
  "Starts a system"
  [env]
  (ig/init (config env)))

(defn stop
  "Stops a system"
  []
  (ig/halt!))

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

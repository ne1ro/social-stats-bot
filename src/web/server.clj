(ns web.server
  (:gen-class) ; for -main method in uberjar
  (:require [integrant.core :as ig]
            [io.pedestal.http :as server]
            [io.pedestal.http.route :as route]
            [web.service :as service]
            [clojure.spec.alpha :as s]))

(def ^:private runned-service (atom nil))
(def ^:private alter-runned-service (partial alter-var-root #'runned-service))

(deref runned-service)

(defn run-dev
  "The entry-point for 'lein run-dev'"
  [& args]
  (println "\nCreating your [DEV] server...")
  (-> service/service ;; start with production configuration
      (merge {:env :dev
              ;; do not block thread that starts web server
              ::server/join? false
              ;; Routes can be a function that resolve routes,
              ;;  we can use this to set the routes to be reloadable
              ::server/routes #(route/expand-routes (deref #'service/routes))
              ;; all origins are allowed in dev mode
              ::server/allowed-origins {:creds true :allowed-origins (constantly true)}
              ;; Content Security Policy (CSP) is mostly turned off in dev mode
              ::server/secure-headers {:content-security-policy-settings {:object-src "'none'"}}})
      ;; Wire up interceptor chains
      server/default-interceptors
      server/dev-interceptors
      server/create-server
      server/start))

;; If you package the service up as a WAR,
;; some form of the following function sections is required (for io.pedestal.servlet.ClojureVarServlet).

;;(defonce servlet  (atom nil))
;;
;;(defn servlet-init
;;  [_ config]
;;  ;; Initialize your app here.
;;  (reset! servlet  (server/servlet-init service/service nil)))
;;
;;(defn servlet-service
;;  [_ request response]
;;  (server/servlet-service @servlet request response))
;;
;;(defn servlet-destroy
;;  [_]
;;  (server/servlet-destroy @servlet)
;;  (reset! servlet nil))
(defmethod ig/init-key :web [_ conf]
  (let [srvc (-> service/service (merge conf) server/create-server)]
    (prn srvc "WEB:" conf)
    (alter-runned-service srvc)
    (server/start srvc)))

(defmethod ig/halt-key! :web [_ _] (server/stop @runned-service))

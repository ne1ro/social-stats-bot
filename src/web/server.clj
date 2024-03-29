(ns web.server
  (:import org.eclipse.jetty.server.Server)
  (:require [integrant.core :as ig]
            ;; [ring.middleware.reload :refer [wrap-reload]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as st]
            [compojure.core :refer [routes POST]]
            [compojure.route :refer [not-found]]
            [ring.adapter.jetty :as jetty]
            [ring.logger :as logger]
            [ring.middleware.json :refer [wrap-json-body]]
            [web.service :as service]))

(s/def ::env #{:dev :test :prod})
(s/def ::port pos-int?)
(s/def ::messenger record?)
(s/def ::use-cases record?)
(s/def ::conf (s/keys :req [::env ::port ::messenger ::use-cases]))

(s/fdef server :args (s/cat :conf ::conf) :ret map?)
(defn server [{messenger ::messenger use-cases ::use-cases}]
  (let [handler (service/get-bot messenger use-cases)
        rs (routes (POST "/handler" {body :body} (handler body))
                   (not-found "Not Found"))]
    (-> rs
        (wrap-json-body {:keywords? true})
        logger/wrap-log-request-start
        logger/wrap-log-request-params
        logger/wrap-log-response)))

(defmethod ig/pre-init-spec :web [_] ::conf)

(defmethod ig/init-key :web [_ {port ::port :as conf}]
  (let [handler (-> conf server delay atom)
        opts {:port port :join? false}]
    {:handler handler
     :server  (jetty/run-jetty (fn [req] (@@handler req)) opts)}))

(defmethod ig/halt-key! :web [_ {:keys [server]}] (.stop ^Server server))

(defmethod ig/suspend-key! :web [_ {:keys [handler]}] (reset! handler (promise)))

(st/instrument)

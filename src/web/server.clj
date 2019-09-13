(ns web.server
  (:require [integrant.core :as ig]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as st]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [morse.api :as telegram]
            ;; [ring.middleware.reload :refer [wrap-reload]
            [ring.adapter.jetty :as jetty]
            [ring.logger :as logger]
            [ring.middleware.json :refer [wrap-json-body]]
            [web.service :as service]))

(s/def ::env #{:dev :test :prod})
(s/def ::port pos-int?)
(s/def ::telegram-token string?)
(s/def ::endpoint string?)
(s/def ::conf
  (s/keys :req [::env ::port ::telegram-token]
          :opt [::endpoint]))

(defn app-routes [token]
  (routes (POST "/handler" {body :body} (service/bot-handler body))
          (route/not-found "Not Found")))

(s/fdef server :args (s/cat :conf ::conf) :ret map?)
(defn server [{token ::telegram-token endpoint ::endpoint}]
  (telegram/set-webhook token endpoint)
  (-> token
      app-routes
      (wrap-json-body {:keywords? true})
      logger/wrap-log-request-start
      logger/wrap-log-request-params
      logger/wrap-log-response))

(defmethod ig/init-key :web [_ {port ::port :as conf}]
  (jetty/run-jetty (-> conf (dissoc :use-cases) server) {:port port}))

(defmethod ig/halt-key! :web [_ server] (.stop server))

(st/instrument)

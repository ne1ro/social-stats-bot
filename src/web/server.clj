(ns web.server
  (:gen-class) ; for -main method in uberjar
  (:require [integrant.core :as ig]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [morse.api :as telegram]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as st]
            [web.service :as service]))

(s/def ::env #{:dev :test :prod})
(s/def ::port pos-int?)
(s/def ::telegram-token string?)
(s/def ::endpoint string?)
(s/def ::conf
  (s/keys :req [::env ::port ::telegram-token]
          :opt [::endpoint]))

(defroutes app-routes
  (POST "/handler" {{updates :result} :body} (map service/bot-handler updates))
  (route/not-found "Not Found"))

(s/fdef server :args (s/cat :conf ::conf) :ret map?)
(defn server [{token ::telegram-token endpoint ::endpoint}]
  (telegram/set-webhook token endpoint)
  app-routes)

(defmethod ig/init-key :web [_ {port ::port :as conf}]
  (jetty/run-jetty (-> conf (dissoc :use-cases) server) {:port port}))

(defmethod ig/halt-key! :web [_ server] (.stop server))

(st/instrument)

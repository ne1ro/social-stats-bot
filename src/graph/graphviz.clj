(ns graph.graphviz
  "Graphviz adapter for the Graph port"
  (:require [social-stats-bot.graph :refer [Graph]]
            [integrant.core :as ig]))

(defrecord Graphviz [opts]
  Graph
  (draw-user-stats [opts user stats params] stats))

(defmethod ig/init-key :graph
  [_ {:keys [graph]}]
  (->Graphviz graph))

(defmethod ig/halt-key! :graph [_ _] nil)

(ns social-provider.instagram
  (:require [social-stats-bot.social-provider :refer [SocialProvider]]
            [integrant.core :as ig]
            [clj-http.client :as http]))

(defn- parse-names [fullname]
  (as-> fullname $ (clojure.string/split $ #" ") (take 2 $)))

(defn- compose-profile
  [{:keys [full_name username profile_pic_url_hd edge_followed_by edge_follow
           edge_owner_to_timeline_media]}]
  (let [[first-name last-name] (parse-names full_name)]
    {:first-name first-name
     :last-name last-name
     :provider :instagram
     :nickname username
     :avatar profile_pic_url_hd
     :posts-count (:count edge_owner_to_timeline_media)
     :followers-count (:count edge_followed_by)
     :followings-count (:count edge_follow)}))

(defrecord Instagram
           [conf]
  SocialProvider

  (fetch-user [{{:keys [base-url suffix]} :conf} nickname]
    (some->
     base-url
     (str nickname suffix)
     (http/get {:accept :json :as :json})
     (get-in [:body :graphql :user])
     compose-profile)))

(defmethod ig/init-key :instagram [_ conf] (->Instagram conf))
(defmethod ig/halt-key! :instagram [_ _] nil)

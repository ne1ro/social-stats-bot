(ns social-provider.instagram
  (:require [social-stats-bot.social-provider :refer [SocialProvider]]
            [clj-http.client :as http]))

(def base-route "https://www.instagram.com/")
(def appendix "/?__a=1")

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

(defn- fetch-acc [nickname]
  (some->
   base-route
   (str nickname appendix)
   (http/get {:accept :json :as :json})
   (get-in [:body :graphql :user])
   compose-profile))

(defrecord Instagram
           [this]
  SocialProvider

  (fetch-user [this nickname] (fetch-acc nickname)))

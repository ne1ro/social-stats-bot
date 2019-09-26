(ns social-stats-bot.user-factory
  (:import (com.github.javafaker Faker)))

(defn build-user [& args]
  (let [f (new Faker)
        default-user {:first-name (-> f .hipster .word)
                      :last-name (-> f .pokemon .name)
                      :posts-count (rand-int 10)
                      :followers-count (rand-int 10)
                      :followings-count (rand-int 10)
                      :avatar (-> f .avatar .image)
                      :last-fetched-at (java.util.Date.)}]
    (if-not (empty? args) (apply assoc (cons default-user args)) default-user)))

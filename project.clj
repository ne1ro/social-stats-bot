(defproject social-stats-bot "0.0.1-SNAPSHOT"
  :description "Bot that sends social network accounts stats"
  :url "http://t.me/SocialStatsBot"
  :dependencies [
		 [org.clojure/clojure "1.10.1"]
     [clj-http "3.10.0"]
     [com.datomic/datomic-free "0.9.5656"]
     [com.fzakaria/slf4j-timbre "0.3.14"]
     [com.github.javafaker/javafaker "0.16"]
     [com.taoensso/timbre "4.10.0"]
     [compojure "1.6.1"]
     [dorothy "0.0.7"]
     [expound "0.7.2"]
     [integrant "0.7.0"]
     [io.aviso/pretty "0.1.37"]
     [morse "0.4.3"]
     [org.clojure/core.async "0.4.490"]
     [org.slf4j/jcl-over-slf4j "1.7.14"]
     [org.slf4j/jul-to-slf4j "1.7.14"]
     [org.slf4j/log4j-over-slf4j "1.7.14"]
     [ring-logger "1.0.1"]
     [ring/ring-jetty-adapter "1.7.1"]
     [ring/ring-json "0.5.0"]
]
  :plugins [[lein-environ "1.1.0"]
            [lein-cljfmt "0.6.4"]
            [lein-kibit "0.1.6"]
            [io.aviso/pretty "0.1.37"]
            [lein-cloverage "1.1.1"]]
  :middleware [io.aviso.lein-pretty/inject]
  :min-lein-version "2.0.0"
  :resource-paths ["config", "resources"]
  :profiles {:dev {:resource-paths ["resources/dev"]
                   :dependencies [[lein-cljfmt "0.6.4"]]}
             :test {:resource-paths ["resources/test"]
                    :dependencies [[lein-cloverage "1.1.1"]]}
             :prod {:resource-paths ["resources/prod"]}
             :uberjar {:aot [social-stats-bot.core]}}
  :main ^{:skip-aot true} social-stats-bot.core)

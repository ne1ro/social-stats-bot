(defproject social-stats-bot "0.0.1-SNAPSHOT"
  :description "Bot that sends social network accounts stats"
  :url "http://t.me/SocialStatsBot"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [ch.qos.logback/logback-classic "1.2.3" :exclusions [org.slf4j/slf4j-api]]
                 [com.github.javafaker/javafaker "0.16"]
                 [dorothy "0.0.7"]
                 [expound "0.7.2"]
                 [integrant "0.7.0"]
                 [io.aviso/pretty "0.1.37"]
                 [io.pedestal/pedestal.jetty "0.5.7"]
                 [io.pedestal/pedestal.service "0.5.7"]
                 [morse "0.4.3"]
                 [orchestra "2018.12.06-2"]
                 [org.clojure/core.async "0.4.490"]
                 [org.jsoup/jsoup "1.12.1"]
                 [org.slf4j/jcl-over-slf4j "1.7.26"]
                 [org.slf4j/jul-to-slf4j "1.7.26"]
                 [org.slf4j/log4j-over-slf4j "1.7.26"]]
  :plugins [[lein-environ "1.1.0"]
            [lein-cljfmt "0.6.4"]
            [lein-kibit "0.1.6"]
            [io.aviso/pretty "0.1.37"]
            [lein-figwheel "0.5.18"]]
  :middleware [io.aviso.lein-pretty/inject]
  :min-lein-version "2.0.0"
  :resource-paths ["config", "resources"]
  :profiles {:dev {:aliases {"run-dev" ["trampoline" "run" "-m" "web.server/run-dev"]}
                   :resource-paths ["resources/dev"]
                   :dependencies [[io.pedestal/pedestal.service-tools "0.5.7"]
                                  [integrant/repl "0.3.1"]]}
             :test {:resource-paths ["resources/test"]}
             :prod {:resource-paths ["resources/prod"]}
             :uberjar {:aot [social-stats-bot.core]}}
  :main ^{:skip-aot true} social-stats-bot.core)

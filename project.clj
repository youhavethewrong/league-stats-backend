(defproject league-stats-backend "0.1.0-SNAPSHOT"
  :description "Backend webservice that serves up tournament stats from official League of Legends tournaments."
  :url "https://github.com/youhavethewrong/league-stats-backend"
  :license {:name "Apache License, Version 2.0"
            :url "https://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/java.jdbc "0.6.1"]
                 [org.clojure/data.json "0.2.6"]
                 [clj-http "3.0.1"]
                 [com.stuartsierra/component "0.3.2"]
                 [com.taoensso/timbre "4.10.0"]
                 [org.xerial/sqlite-jdbc "3.20.1"]
                 [compojure "1.6.0"]
                 [ring/ring-core "1.6.0"]
                 [ring/ring-jetty-adapter "1.6.0"]
                 [ring-json-response "0.2.0"]
                 [ring-cors "0.1.11"]
                 [ring/ring-defaults "0.2.1"]
                 [javax.servlet/servlet-api "2.5"]]
  :main ^:skip-aot league-stats-backend.core
  :profiles {:dev {:dependencies [[ring/ring-mock "0.3.0"]]}
             :uberjar {:aot :all}})

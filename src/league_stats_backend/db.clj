(ns league-stats-backend.db
  (:require [clojure.java.jdbc :as jdbc]
            [taoensso.timbre :as log]))

(defn get-leagues
  "Get all the leagues we know about."
  [db]
  (jdbc/query db ["select * from league"]))

(defn get-tournaments
  "Get all tournaments for a given league."
  [db league-id]
  (jdbc/query db ["select * from tournament where league_api_key = ?" league-id]))

(defn get-all-tournaments
  "Get all tournaments."
  [db]
  (jdbc/query db ["select * from tournament"]))

(defn get-stats-for-tournament
  "Get all the player stats for a given tournament."
  [db tournament-key]
  (jdbc/query db ["select * from player_stat where tournament_api_key = ?" tournament-key]))

(defn refresh-tournaments
  "Freshen tournaments from the external API."
  [db tournaments]
  (jdbc/execute! db ["delete from tournament"])
  (log/info "Cleared out tournaments.")
  (jdbc/insert-multi! db :tournament tournaments)
  (log/info "Stored latest tournaments.")
  {:tournaments (count tournaments)})

(defn refresh-stats
  "Freshen player stats from the external API."
  [db stats]
  (jdbc/execute! db ["delete from player_stat"])
  (log/info "Cleared out player stats.")
  (jdbc/insert-multi! db :player_stat stats)
  (log/info "Stored latest player stats.")
  {:players (count stats)})

;; Self-initialization

(defn create-schema
  "Define the tables in our database."
  [db]
  (jdbc/execute! db
   ["create table league (id integer primary key, name varchar(255), api_key varchar(255), last_modified datetime default CURRENT_TIMESTAMP)"])
  (jdbc/execute! db
   ["create table tournament (id integer primary key, title varchar(255), api_key varchar(255), league_api_key varchar(255), last_modified datetime default CURRENT_TIMESTAMP)"])
  (jdbc/execute! db
   ["create table player_stat (id integer primary key, tournament_api_key varchar(255), name varchar(255), team varchar(255), position varchar(255), games_played varchar(255), kills varchar(255), deaths varchar(255), assists varchar(255), last_modified datetime default CURRENT_TIMESTAMP)"]))

(defn destroy-schema
  "Clear out the database."
  [db]
  (jdbc/execute! db ["drop table if exists player_stat"])
  (jdbc/execute! db ["drop table if exists tournament"])
  (jdbc/execute! db ["drop table if exists league"]))

(defn load-leagues
  "Load the leagues I'm aware of into the db.  I haven't found an API resource for this yet."
  [db]
  (let [leagues [{:name "ALL-STARS" :api_key 1}
                 {:name "NA" :api_key 2}
                 {:name "EU" :api_key 3}
                 {:name "NA-CS" :api_key 4}
                 {:name "EU-CS" :api_key 5}
                 {:name "LCK" :api_key 6}
                 {:name "LPL" :api_key 7}
                 {:name "LMS" :api_key 8}
                 {:name "WORLDS" :api_key 9}
                 {:name "MSI" :api_key 10}
                 {:name "IWQ" :api_key 12}
                 {:name "OPL" :api_key 13}]]
    (jdbc/insert-multi! db :league leagues)))

(defn recreate-db
  [db]
  (destroy-schema db)
  (create-schema db)
  (load-leagues db))

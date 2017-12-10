(ns league-stats-backend.db
  (:require [clojure.java.jdbc :as jdbc]
            [taoensso.timbre :as log]))

(defn define-schema
  "Define the tables in our database."
  [db]
  (jdbc/execute! db
   ["create table league (id integer primary key, name varchar(255), api_key integer, last_modified datetime default CURRENT_TIMESTAMP)"])
  (jdbc/execute! db
   ["create table tournament (id integer primary key, name varchar(255), api_key varchar(255), league_api_key integer, last_modified datetime default CURRENT_TIMESTAMP)"])
  (jdbc/execute! db
   ["create table player_stat (id integer primary key, tournament_api_key varchar(255), name varchar(255), team varchar(255), position varchar(255), games_played integer, kills integer, deaths integer, assists integer, last_modified datetime default CURRENT_TIMESTAMP)"]))

(defn destroy-schema
  "Clear out the database."
  [db]
  (jdbc/execute! db ["drop table if exists player_stat"])
  (jdbc/execute! db ["drop table if exists tournament"])
  (jdbc/execute! db ["drop table if exists league"]))

(defn get-leagues
  "Get all the leagues we know about."
  [db]
  (jdbc/query db ["select * from league"]))

(defn get-tournaments
  "Get all tournaments for a given league."
  [db league-id]
  (jdbc/query db ["select * from tournament where league_api_key = ?" league-id]))

(defn insert-stats
  "Add new player stats for a given tournament."
  [db data]
  (try
    (jdbc/insert! db :player_stat data)
    (catch Exception ex
      (log/error (str "Could not insert data " data))
      (log/error (str "Exception message was: " (.getMessage ex))))))

(defn get-stats-for-tournament
  "Get all the player stats for a given tournament."
  [db tournament-key]
  (jdbc/query db ["select * from player_stat where tournament_api_key = ?" tournament-key]))

(defn refresh-tournaments
  "Freshen tournaments from the external API."
  [db tournaments]
  (log/info "Tournaments")
  (log/info (first tournaments))
  (jdbc/execute! db ["delete from tournament"])
  (log/info "Cleared out tournaments.")
  (jdbc/insert-multi! db :tournament tournaments)
  {:status "ok" :tournaments_stored (count tournaments)})

(defn refresh-stats
  "Freshen player stats from the external API."
  [db stats]
  (jdbc/insert-multi! db :player_stat stats))

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

(ns league-stats-backend.resource
  (:require [clojure.string :as string]
            [compojure.route :as route]
            [taoensso.timbre :as log]
            [league-stats-backend.client :as client]
            [league-stats-backend.db :as db]
            [clojure.string :as str]))

(defn only-digits?
  "Determines if a string contains only digits."
  [s]
  (and (not (string/blank? s))
       (every? #(Character/isDigit %) s)))

(defn id?
  "Determines if a string looks like a tournament ID.  It appears that they are UUIDs."
  [s]
  (and (not (string/blank? s))
       (re-matches #"(?i)([a-f0-9]+\-){4}[a-f0-9]+" s)))

(defn not-found
  "Log a message and return a 404 with a message in the body."
  [message]
  (log/debug message)
  (route/not-found message))

(defn get-leagues
  "Retrieve our leagues from the db."
  [{:keys [db]}]
  (db/get-leagues db))

(defn get-tournaments
  "Retrieve all tournaments for a specific league from the db."
  [{:keys [db]} league-id]
  (if (only-digits? league-id)
    (db/get-tournaments db league-id)
    {:status 400
     :body {:message (str "League ID must be a number, but was given '" league-id "'.")}}))

(defn get-all-tournaments
  "Retrieve all tournaments from the db."
  [{:keys [db]}]
  (db/get-all-tournaments db))

(defn get-stats
  "Retrieve all player stats for a tournament from the db.  Use outer stats key for drop-in replacement of external API."
  [{:keys [db]} tournament-id]
  (if (id? tournament-id)
    {:body {:stats (db/get-stats-for-tournament db (string/lower-case tournament-id))}}
    {:status 400
     :body {:message (str "Tournament ID must be in UUID form, but was given '" tournament-id "'.")}}))

(defn- get-all-tournaments-external
  "Find all tournaments for all leagues from the external API."
  [config leagues]
  (flatten
   (map
    (fn [{:keys [api_key]}]
      (let [tournaments (client/get-tournaments config api_key)]
        (map #(assoc % :league_api_key api_key) tournaments)))
    leagues)))

(defn refresh-tournaments
  "Update our database with all the tournaments for all the leagues available from the external API."
  [{:keys [db] :as config}]
  (let [leagues (get-leagues config)
        tournaments (get-all-tournaments-external config leagues)
        tc (count tournaments)
        lc (count leagues)]
    (log/info "Synching" tc "tournaments from" lc "leagues.")
    {:body (db/refresh-tournaments db tournaments)}))

(defn get-all-player-stats
  [config tournaments]
  (flatten
   (map
    (fn [{:keys [api_key]}]
      (let [player-stats (client/get-player-stats config api_key)]
        (map #(assoc % :tournament_api_key api_key) player-stats)))
    tournaments)))

(defn refresh-stats
  "Update our database with all the player stats from all the tournaments for all the leagues from the external API."
  [{:keys [db] :as config}]
  (let [leagues (get-leagues config)
        tournaments (get-all-tournaments-external config leagues)
        player-stats (get-all-player-stats config tournaments)
        tc (count tournaments)
        pc (count player-stats)]
    (log/info "Synching" pc "player stats from" tc "tournaments.")
    {:body (db/refresh-stats db player-stats)}))

(ns league-stats-backend.resource
  (:require [compojure.route :as route]
            [taoensso.timbre :as log]
            [league-stats-backend.client :as client]
            [league-stats-backend.db :as db]))

(defn not-found
  "Logs a message and returns a 404 with the message in the body."
  [message]
  (log/debug message)
  (route/not-found message))

(defn get-leagues
  [{:keys [db]}]
  (db/get-leagues db))

(defn get-tournaments
  [{:keys [db]} league-id]
  (db/get-tournaments db league-id))

(defn get-stats
  [config tournament-id]

  )

(defn get-all-tournaments
  "Find all tournaments for all leagues from the external API."
  [config leagues]
  (flatten
   (map
    (fn [league-id]
      (let [tournaments (client/get-tournaments config league-id)]
        (log/info (str "Got " (count tournaments)) )
        (map #(assoc % :league_api_key league-id) tournaments)))
    leagues)))

(defn refresh-tournaments
  [{:keys [db] :as config}]
  (let [leagues (get-leagues config)
        _ (log/info "Got" (count leagues) "leagues")
        tournaments (get-all-tournaments config leagues)
        _ (log/info "Got" (count tournaments) "for all leagues")]
    (db/refresh-tournaments db tournaments)))

(defn refresh-stats
  [config]

  )

(ns league-stats-backend.client
  (:require [clj-http.client :as http]
            [clojure.set :refer [rename-keys]]
            [clojure.data.json :as json]
            [taoensso.timbre :as log]))

(defn parse-schedule-body
  "Parse and rename the important keys from the tournament response."
  [body]
  (let [data (json/read-str body :key-fn keyword)
        tournaments (:highlanderTournaments data)
        relevant-data (fn [tournament] (-> tournament
                                           (select-keys [:id :title])
                                           (rename-keys {:id :api_key})))]
    (log/info "Extracted" (count tournaments) "tournaments." )
    (map relevant-data tournaments)))

(defn get-tournaments
  "Retrieve information about tournaments for a given league from the external API."
  [{:keys [schedule-items-url user-agent]} league-id]
  (let [league-url (str schedule-items-url league-id)
        response (http/get league-url
                  {:client-params {"http.useragent" user-agent}
                   :throw-exceptions false})]
    (log/info "Looking for tournaments at" league-url)
    (if (= 200 (:status response))
      (parse-schedule-body (:body response))
      (do
        (log/warn "Received" (:status response) "from call to url" league-url)
        []))))

(defn parse-stats-body
  "Parse and rename the important keys from the player stats response."
  [body]
  (let [data (json/read-str body :key-fn keyword)
        stats (:stats data)
        main-stats (fn [player] (-> player
                                    (select-keys [:name :team :position :gamesPlayed :kills :deaths :assists])
                                    (rename-keys {:gamesPlayed :games_played})))]
    (log/info "Found stats for" (count stats) "players.")
    (map main-stats stats)))

(defn get-player-stats
  "Retrieve information about player stats for a given tournament from the external API."
  [{:keys [tournament-stats-url user-agent]} tournament-id]
  (let [stats-url (str tournament-stats-url tournament-id)
        response (http/get stats-url
                           {:client-params {"http.useragent" user-agent}
                            :throw-exceptions false})]
    (log/info "Looking for player stats at" stats-url)
    (if (= 200 (:status response))
      (parse-stats-body (:body response))
      (do
        (log/warn "Received" (:status response) "from call to url" stats-url)
        []))))

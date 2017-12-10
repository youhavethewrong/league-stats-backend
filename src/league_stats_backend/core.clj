(ns league-stats-backend.core
  (:require [clojure.java.io :as io]
            [taoensso.timbre :as log]
            [compojure.core :refer [routes GET POST]]
            [compojure.handler :as handler]
            [ring.middleware.json-response :as json-response]
            [ring.middleware.params :as params]
            [league-stats-backend.client :as client]
            [league-stats-backend.db :as db]
            [league-stats-backend.resource :as resource]
            [league-stats-backend.system :as system]
            [com.stuartsierra.component :as component]))

(def config
  {:port 8337
   :user-agent "League Stats v0.0.1"
   :tournament-stats-url "http://api.lolesports.com/api/v2/tournamentPlayerStats?tournamentId="
   :schedule-items-url "http://api.lolesports.com/api/v1/scheduleItems?leagueId="
   :db {:dbtype "sqlite"
        :dbname "league-stats.db"
        :username "sa"
        :password "superSecureYou'llNeverGuess"}})

(defn build-routes
  [config]
  (routes
   (GET "/leagues" []
        (resource/get-leagues config))
   (GET "/tournaments/:league-id" [league-id]
        (resource/get-tournaments config league-id))
   (GET "/stats/:tournament-id" [tournament-id]
        (resource/get-stats config tournament-id))
   (POST "/tournaments" []
         (resource/refresh-tournaments config))
   (POST "/stats" []
         (resource/refresh-stats config))
   {:status 404
    :headers {"Content-Type" "text/plain"}
    :body "No resource is available here."}))

(defn start
  [config]
  (log/info "[Starting!]")
  (log/info (str "\n" (slurp (io/resource "banner.txt"))))
  (let [handler (-> (build-routes config)
                    params/wrap-params
                    json-response/wrap-json-response)]
    (system/system (assoc config :handler handler))))

(defn -main
  [& args]
  (component/start (start config)))

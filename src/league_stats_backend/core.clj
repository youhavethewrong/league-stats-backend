(ns league-stats-backend.core
  (:require [clojure.java.io :as io]
            [taoensso.timbre :as log]
            [compojure.core :refer [routes GET POST]]
            [compojure.handler :as handler]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.cors :refer [wrap-cors]]
            [league-stats-backend.client :as client]
            [league-stats-backend.db :as db]
            [league-stats-backend.resource :as resource]
            [league-stats-backend.system :as system]
            [com.stuartsierra.component :as component])
  (:gen-class))

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
   (GET "/tournaments" []
        (resource/get-all-tournaments config))
   (GET "/tournaments/:league-id" [league-id]
        (resource/get-tournaments config league-id))
   (GET "/stats/:tournament-id" [tournament-id]
        (resource/get-stats config tournament-id))
   (POST "/tournaments" []
         (resource/refresh-tournaments config))
   (POST "/stats" []
         (resource/refresh-stats config))
   (resource/not-found "No resource is available here.")))

(defn wrap-request-logging
  [handler]
  (fn [{:keys [remote-addr request-method uri] :as request}]
    (let [start (System/currentTimeMillis)
          response (handler request)
          status (:status response)
          finish (System/currentTimeMillis)
          total (- finish start)]
      (log/info (format "%s %s %s %s (%dms)" remote-addr request-method uri status total))
      response)))

(defn start
  [config]
  (log/info "[Starting!]")
  (log/info (str "\n" (slurp (io/resource "banner.txt"))))
  (let [handler (-> (build-routes config)
                    (wrap-cors :access-control-allow-origin [#".*"]
                               :access-control-allow-methods [:get :post])
                    wrap-request-logging
                    wrap-params
                    wrap-json-response)]
    (system/system (assoc config :handler handler))))

(defn -main
  [& args]
  (component/start (start config)))

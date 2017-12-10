(ns league-stats-backend.client
  (:require [clj-http.client :as http]
            [clojure.set :refer [rename-keys]]
            [clojure.data.json :as json]
            [taoensso.timbre :as log]))

(defn parse-schedule-body
  [body]
  (let [data (json/read-str body :key-fn keyword)
        tournaments (:highlanderTournaments data)
        relevant-data (fn [tournament] (-> (select-keys [:id :title])
                                           (rename-keys {:id :tournament_api_key})))]
    (log/info "Got these tournaments" (count tournaments))
    (map relevant-data tournaments)))

(defn get-tournaments
  [{:keys [schedule-items-url user-agent]} league-id]
  (let [league-url (str schedule-items-url league-id)
        response (http/get league-url
                  {:client-params {"http.useragent" user-agent}
                   :throw-exceptions false})]
    (if (= 200 (:status response))
      (do
        (log/info "Received successful response from " league-url)
        (parse-schedule-body (:body response)))
      (do
        (log/warn (str "Received " (:status response) " from call to url " ))
        []))))

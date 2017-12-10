(ns league-stats-backend.system
  (:require [com.stuartsierra.component :as component]
            [taoensso.timbre :as log]
            [ring.adapter.jetty :as jetty]))

(defrecord Webservice
    [handler port]
  component/Lifecycle
  (start [component]
    (log/info "Starting webservice...")
    (let [webservice (jetty/run-jetty handler {:port port :join? false})]
      (assoc component :webservice webservice)))
  (stop [component]
    (when-let [ws (:webservice component)]
      (log/info "Stopping webservice...")
      (.stop ws))
    (assoc component :webservice nil)))

(defn new-webservice
  [handler port]
  (map->Webservice {:handler handler :port port}))

(defn system
  [{:keys [handler port]}]
  (component/system-map :scheduler (new-webservice handler port)))

(ns league-stats-backend.client-test
  (:require [league-stats-backend.client :as client]
            [clojure.test :refer [deftest testing is]]
            [clojure.java.io :as io]))

(def full-schedule-body
  (slurp (io/resource "schedule-items.json")))

(deftest parse-schedule-items
  (testing "Should fish up tournament IDs and names from a big blob of json."
    (is (= 3 (count (client/parse-schedule-body full-schedule-body))))))

(def full-stats-body
  (slurp (io/resource "tournament-player-stats.json")))

(deftest parse-player-stats
  (testing "Should extract player stats from the tournament stats json."
    (is (= 40 (count (client/parse-stats-body full-stats-body))))))

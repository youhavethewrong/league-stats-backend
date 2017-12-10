(ns league-stats-backend.client-test
  (:require [league-stats-backend.client :as client]
            [clojure.test :refer [deftest testing is]]
            [clojure.java.io :as io]))

(def full-body
  (slurp (io/resource "schedule-items.json")))

(deftest parse-schedule-items
  (testing "Should fish up tournament IDs and names from a big blob of json."
    (is (= 3 (count )))))

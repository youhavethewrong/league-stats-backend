(ns league-stats-backend.resource-test
  (:require [league-stats-backend.resource :as r]
            [clojure.test :refer [deftest testing is]]))

(deftest id-tests
  (testing "Should confirm a league ID is an ID."
    (is (r/id? "41a602f2-4e4d-4306-949c-e5919ed79628"))
    (is (r/id? "41A602F2-4E4D-4306-949C-E5919ED79628")))
  (testing "Should reject that these are IDs."
    (is (not (r/id? "little-bobby';drop tables")))
    (is (not (r/id? "")))
    (is (not (r/id? nil)))))

(deftest number-tests
  (testing "Should confirm an only-digits string."
    (is (r/only-digits? "22")))
  (testing "Should reject non-only-digits strings."
    (is (not (r/only-digits? "taco")))
    (is (not (r/only-digits? "")))
    (is (not (r/only-digits? nil)))
    (is (not (r/only-digits? "99.0")))))

(ns clojure-orbit.test.physics.object
  (:use [clojure.test])
  (:require [clojure-orbit.physics.coordinates :as coordinates]
            [clojure-orbit.physics.vector      :as vector]
            [clojure-orbit.physics.object      :as object]))

(deftest object-test
  (testing "center-of-mass"
    (is (= [1 0]
             (object/center-of-mass
              {:pos [0 0] :mass 1}
              {:pos [2 0] :mass 1})))
    (is (= [1.5 1.5]
             (object/center-of-mass
              {:pos [1 1] :mass 10}
              {:pos [2 2] :mass 10})))))

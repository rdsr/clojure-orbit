(ns clojure-orbit.test.physics.coordinates
  (:use [clojure.test]
        [clojure-orbit.physics.coordinates]))

(deftest coordinates-test
  (testing "add"
    (is (= [2 2] (add [1 1] [1 1]))))

  (testing "subtract"
    (is (= [1 1] (subtract [3 4] [2 3]))))

  (testing "distance"
    (is (= 1 (distance [0 0] [0 1])))
    (is (= 1 (distance [1 0] [2 0])))
    (is (= 5 (distance [0 0] [3 4]))))

  (testing "rotate"
    (is (= origin (rotate origin 270)))
    (is (= [-1 3] (rotate [1 3] 90))))

  (testing "scale"
    (is (= [2 2] (scale [1 1] 2))))

  (testing "average"
    (let [pos [2 2]]
      (is (= pos (average pos pos))))))

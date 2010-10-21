(ns clojure-orbit.physics.vector
  "implementing vectors with the
   help of the coordinates ns"
  (:require [clojure-orbit.physics.coordinates :as coordinates]))

(defn make [x y] [x y])

(def zero-vector coordinates/origin)

(def zero-mag? coordinates/origin?)

(defn magnitude [v]
  (coordinates/distance coordinates/origin v))

(defn unit [v]
  (coordinates/scale v (/ 1 (magnitude v))))

(defn rotate90 [v] (coordinates/rotate v 90))

(def add coordinates/add)

(def subtract coordinates/subtract)

(def scale coordinates/scale)

(ns clojure-orbit.Main
  (:use [clojure-orbit.orbit.universe])
  (:gen-class))

(defn -main [& _] (run-universe))

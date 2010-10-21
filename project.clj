(defproject clojure-orbit "1.0"
  :url "http://github.com/rdsr/clojure-orbit"
  :description "Learning/refactoring Uncle Bob's Orbital Simulater in Clojure
                see http://github.com/unclebob/clojureOrbit"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]]
  :dev-dependencies [[swank-clojure "1.2.1"]]
  :test-path "test"
  :aot [clojure-orbit.Main]
  :main clojure-orbit.Main)

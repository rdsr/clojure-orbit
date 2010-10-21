(ns clojure-orbit.physics.coordinates
  "coordinates is same as the physics.position ns,
   (see http://github.com/unclebob/clojureOrbit/
    blob/master/src/physics/position.clj)

   just renamed (and refactored) so that I don't
   have to implement vector separately, but can
   be extended from coordinates itself")

(def origin [0 0])

(defn origin? [p]
  (every? zero? p))

(defn add
  [[x1 y1] [x2 y2]]
  [(+ x1 x2) (+ y1 y2)])

(defn subtract
  [[x1 y1] [x2 y2]]
  [(- x1 x2) (- y1 y2)])

(defn distance
  [[x1 y1] [x2 y2]]
  (Math/sqrt
   (+ (Math/pow (- x1 x2) 2)
      (Math/pow (- y1 y2) 2))))

(defn average
  [[x1 y1] [x2 y2]]
  [(/ (+ x1 x2) 2) (/ (+ y1 y2) 2)])

(defn scale [[x y] m] [(* m x) (* m y)])

(defn rotate
  [[x y] angle]
  (case (rem angle 360)
        0   [ x       y ]
        90  [(- x)    y ]
        180 [(- x) (- y)]
        270 [x     (- y)]))

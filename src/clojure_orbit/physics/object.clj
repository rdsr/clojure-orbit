(ns clojure-orbit.physics.object
  (:use clojure.contrib.combinatorics)
  (require [clojure-orbit.physics.coordinates :as coordinates]
           [clojure-orbit.physics.vector :as vector]))

(defrecord object
  [pos mass velocity force name])

(defn make
  [pos mass velocity force name]
  (object. pos mass velocity force name))

(defn gravity [m n r]
  (/ (* m n)
     (* r r)))

(defn force-between [o1 o2]
  (let [p1 (:pos o1)
        p2 (:pos o2)
        d  (coordinates/distance p1 p2)
        uv (vector/unit (vector/subtract p2 p1))
        g  (gravity (:mass o1) (:mass o2) d)]
    (vector/scale uv g)))

(defn accumulate-forces
  ([o os]
     (assoc o
       :force
       (reduce #(vector/add %1 (force-between o %2))
               vector/zero-vector
               (remove #(= o %) os))))
  ([os]
     (map #(accumulate-forces % os) os)))

(defmulti accelerate seq?)

(defmethod accelerate false
  [o]
  (let [f  (:force o)
        m  (:mass o)
        v  (:velocity o)
        av (vector/add v (vector/scale f (/ 1.0 m)))]
    (assoc o :velocity av)))

(defmethod accelerate true
  [os] (map accelerate os))

(defmulti reposition seq?)

(defmethod reposition false
  [o]
  (let [p (:pos o)
        v (:velocity o)]
    (assoc o :pos (coordinates/add p v))))

(defmethod reposition true
  [os] (map reposition os))

(defn center-of-mass
  [{p1 :pos, m1 :mass}
   {p2 :pos, m2 :mass}]
  (let [vd  (vector/subtract p1 p2)
        r2  (* (vector/magnitude vd)
               (/ m1 (+ m1 m2)))
        vr2 (vector/scale (vector/unit vd) r2)]
    (vector/add p2 vr2)))

(defn collide?
  [{p1 :pos} {p2 :pos}]
  (>= 3 (coordinates/distance p1 p2)))

(defn merge-objects
  [{n1 :name, m1 :mass, v1 :velocity f1 :force, :as o1}
   {n2 :name, m2 :mass, v2 :velocity f2 :force, :as o2}]
  (let [p   (center-of-mass o1 o2)
        m   (+ m1 m2)
        mv1 (vector/scale v1 m1)
        mv2 (vector/scale v2 m2)
        v   (vector/scale (vector/add mv1 mv2) (/ 1 m))
        f   (vector/add f1 f2)
        n   (if (> m1 m2)
              (str n1 "." n2)
              (str n2 "." n1))]
    (make p m v f n)))

(defn merge-collisions
  [os]
  (loop [pairs (combinations os 2)
         objects (set os)]
      (if (empty? pairs)
        (seq objects)
        (let [[m n] (first pairs)]
          (if (collide? m n)
            (let [merged (merge-objects m n)]
              (recur (rest pairs)
                     (-> objects
                         (disj m n)
                         (conj merged))))
            (recur (rest pairs) objects))))))

(defn update [os]
  (-> os merge-collisions accumulate-forces accelerate reposition))

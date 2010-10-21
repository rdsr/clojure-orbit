(ns clojure-orbit.orbit.universe
  (:import (java.awt Color Dimension Graphics)
           (javax.swing JPanel JFrame Timer JOptionPane)
           (java.awt.event ActionEvent ActionListener KeyListener))
  (:use [clojure.contrib.import-static])
  (require [clojure-orbit.physics.object      :as object])
  (require [clojure-orbit.physics.vector      :as vector])
  (require [clojure-orbit.physics.coordinates :as coordinates]))

(import-static java.awt.event.KeyEvent VK_LEFT VK_RIGHT VK_UP VK_DOWN)

(def center [500 500])
(defrecord controls
  [magnification center trails clear])

(defn size-by-mass
  [{m :mass}]
  (Math/sqrt m))

(defn color-by-mass [{m :mass}]
  (condp > m
    1  Color/black
    2  (Color. 210 105 30)
    5  Color/red
    10 (Color. 107 142 35)
    20 Color/magenta
    40 Color/blue
    (Color. 255 215 0)))

(defn find-sun [universe]
  (->> universe
       (filter #(re-find #"sun" (:name %)))
       first))

(defn random-velocity
  [object-pos sun-pos]
  (let [sd        (coordinates/distance object-pos sun-pos)
        v         (Math/sqrt (/ 1 sd))
        direction (-> object-pos
                      (vector/subtract sun-pos)
                      vector/unit
                      vector/rotate90)]
    (vector/scale
     direction (+ (rand 0.01)
                  (* v 13.5)))))

(defn random-position
  [sun-pos]
  (let [r (+ (rand 300) 30)    ;; why not just use (rand 330) ?
        theta (rand (* 2 Math/PI))]
    (coordinates/add sun-pos
                     [(* r (Math/cos theta))
                      (* r (Math/sin theta))])))

(defn random-object
  [{sun-pos :pos} n]
  (let [pos (random-position sun-pos)]
    (object/make pos
                 (rand 0.1)
                 (random-velocity pos sun-pos)
                 vector/zero-vector
                 (str "r" n))))

(defn create-universe []
  (let [v0  vector/zero-vector
        sun (object/make center 150 v0 v0 "sun")]
    (cons sun
          (map #(random-object sun %)
               (range 0 100)))))


;; --- mutable code follows

(defn update-universe
  [universe]
  (swap! universe object/update))

(defn magnify
  [factor controls universe]
  (let [sun-pos (:pos (find-sun @universe))
        new-mag (* factor (:magnification @controls))]
    (swap! controls
           assoc
           :magnification new-mag
           :center sun-pos           ;; why update this ?
           :clear true)))            ;; ditto

(defn reset-screen-state
  [controls]
  (swap! controls assoc :clear false))

(defn toggle-trail
  [controls]
  (swap! controls
         #(assoc % :trails (not (:trails %)))))

(defn handle-key
  [c universe controls]
  (case c
        \q     (System/exit 0)
        \+     (magnify 1.1 controls universe)
        \-     (magnify 0.9 controls universe)
        \space (magnify 1.0 controls universe)
        \t     (toggle-trail controls)
        nil))

(defn draw-object
  [^Graphics graphics obj controls]
  (let [mag        (:magnification controls)
        sun-center (:center controls)
        x-offset   (- (center 0)
                      (* mag (sun-center 0)))
        y-offset   (- (center 1)
                      (* mag (sun-center 1)))
        x          (+ x-offset
                      (* mag ((:pos obj) 0)))
        y          (+ y-offset
                      (* mag ((:pos obj) 1)))
        s          (max 2
                        (* mag (size-by-mass obj)))
        half-s     (/ s 2)
        c          (color-by-mass obj)]
    (.setColor graphics c)
    (.fillOval graphics (- x half-s) (- y half-s) s s)))


(defn draw-universe
  [^Graphics grahics universe controls]
  (doseq [obj universe]
    (draw-object grahics obj controls))
  (.clearRect grahics 0 0 1000 20)
  (.drawString grahics
               (format "Objects: %d, Magnification: %4.3g"
                       (count universe)
                       (:magnification controls))
               20 20))

(defn universe-panel
  [frame universe controls]
  (proxy [JPanel ActionListener KeyListener] []
    (paintComponent
     [^Grahics graphics]
     (when (or (:clear @controls)
               (not (:trails @controls)))
       (proxy-super paintComponent graphics))
     (draw-universe graphics @universe @controls)
     (reset-screen-state controls))

    (actionPerformed
     [^ActionEvent e]
     (update-universe universe)
     (.repaint this))

    (keyPressed
     [^ActionEvent e]
     (handle-key (.getKeyChar e) universe controls)
     (.repaint this))

    (getPreferredSize
     []
     (Dimension. 1000 1000))

    (keyReleased [e])
    (keyTyped [e])))

(defn universe-frame []
  (let [controls (atom (controls. 1.0 center false false))
        universe (atom (create-universe))
        frame (JFrame. "Orbit")
        panel (universe-panel frame universe controls)
        timer (Timer. 1 panel)]
    (doto panel
      (.setFocusable true)
      (.addKeyListener panel))
    (doto frame
      (.add panel)
      (.pack)
      (.setVisible true)
      (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE))
    (.start timer)))

(defn run-universe []
  (universe-frame))

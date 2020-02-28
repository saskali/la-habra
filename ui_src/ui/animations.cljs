(ns ui.animations
  (:require [clojure.string :refer [split join]]))


;; ------------------------------------------------------ ;;
;; --------------------- ANIMATIONS --------------------- ;;
;; ------------------------------------------------------ ;;

;; -------------------- CSS MANIP HELPERS ------------------

(defn make-body
  [att values]
  (let [decorated-att (str att ": ")
        decorated-vals (map #(str % ";") values)]
    (map join (partition 2
               (interleave
                 (repeat (count decorated-vals) decorated-att)
                 decorated-vals)))))

(defn splice-bodies
  [& bodies]
  (->> bodies
    (map #(split % #";"))
    (apply map vector)
    (apply map #(str % ";" %2 ";"))))

(defn frames-and-bodies
  [frames bodies]
  (->> bodies
      (map #(apply str % " }"))
      (interleave (map #(str % "% { ") frames))
      (apply str)))

;; get the key frames string, append it to the stylesheet, return name
(defn make-frames!
  [name frames bodies]
  (let [sheet (aget js/document "styleSheets" "0")
        sheet-length (aget sheet "cssRules" "length")
        keyframes (str "@keyframes " name "{ " (frames-and-bodies frames bodies) "}")]
       (.insertRule sheet keyframes sheet-length)
       name))

(defn seconds-to-frames
  [seconds]
  (* 2 seconds))

(defn frames-to-seconds
  [frames]
  (* 0.5 frames))

;; -------------------- SHAPE ANIMATION SWITCHER ---------------------------

(defonce ran (atom {}))

(defn anim-and-hold
  [name frame duration fader solid]
  (let [init-frame (@ran name)
        ran? (and init-frame (<= (+ init-frame (seconds-to-frames duration)) frame))
        ret (if ran? solid fader)]
    (when-not init-frame (swap! ran assoc name frame))
    ret))


;; -------------------- SHAPE ANIMATION HELPER ---------------------------

(defn anim
  ([name duration ms count shape] (anim name duration ms count {} shape))
  ([name duration ms count opts shape]
   (let [animations
         { :animation-name name
           :animation-fill-mode "forwards"
           :animation-duration (str (* duration ms) "s")
           :animation-iteration-count count
           :animation-delay (or (:delay opts) 0)
           :animation-timing-function (or (:timing opts) "ease")}]
        (update-in shape [:style] #(merge % animations)))))

;; -------------------- SOME BASE KEYFRAMES ---------------------------

(defn back-and-forth!
  [name start-str finish-str]
  (make-frames! name [0 50 100]
                (make-body "transform" [(str start-str)
                                        (str finish-str)
                                        (str start-str)])))

(defn a-to-b!
  [name att start-str finish-str]
  (make-frames! name [0 100]
                (make-body att [(str start-str)
                                (str finish-str)])))

(defn fade-start!
  [name op-end]
  (make-frames! name [0 99 100]
                (make-body "fill-opacity" [(str 0)
                                           (str 0)
                                           (str op-end)])))


(make-frames!
  "fade-in-out"
  [0 10 20 30 40 50 60 70 80 90 100]
  (make-body "fill-opacity" [1 0.8 0.6 0.4 0.2 0 0.2 0.4 0.6 0.8 1]))

(make-frames!
  "fade-in-out-irregular"
  [0 4 8 50 54 94]
  (make-body "fill-opacity" [1 0.7 0.5 0.2 0 1]))

(make-frames!
  "fade-out"
  [0 100]
  (make-body "fill-opacity" [1 0]))

(make-frames!
  "fade-in"
  [0 30 80 90 100]
  (make-body "fill-opacity" [0 0 0.5 1 1]))

(make-frames!
  "wee-oo"
  [0 17 37 57 100]
  (make-body "transform"
    [ "translateX(1%) scale(1)"
      "translateX(60%) scale(1.4)"
      "translateX(70%) scale(2.5)"
      "translateX(100%) scale(13.9)"
      "translateX(1%) scale(1)"]))

(make-frames!
  "rot"
  [0 100]
  (make-body "transform" ["rotate(0deg)" "rotate(360deg)"]))

(make-frames!
  "smooth-rot"
  [0 25 50 75 100]
  (make-body "transform"
             ["rotate(0deg)"
              "rotate(90deg)"
              "rotate(180deg)"
              "rotate(270deg)"
              "rotate(360deg)"]))

(make-frames!
  "cent-rot"
  [0 100]
  (make-body "transform" ["translate(300px, 300px) scale(6.2) rotate(0deg)"
                          "translate(300px, 300px) scale(1.2) rotate(360deg)"]))

(make-frames!
  "rev"
  [0 100]
  (make-body "transform" ["rotate(0deg)" "rotate(-360deg)"]))

(fade-start! "fi" 1)

(make-frames!
  "etof"
  [0 100]
  (make-body "transform" ["translateY(10px)" "translateY(1080px)"]))

(make-frames!
  "etof2"
  [0 50 70 100]
  (make-body "transform" ["translateY(20px)"
                          "translateY(300px)"
                          "translateY(600px)"
                          "translateY(1000px)"]))

(back-and-forth! "scaley" "scale(1)" "scale(15)")
(back-and-forth! "scaley-huge" "scale(20)" "scale(50)")

(make-frames!
  "scaley-up"
  [0 25 50 75 100]
  (make-body "transform" ["scale(1)"
                          "scale(1.5)"
                          "scale(2)"
                          "scale(2.5)"
                          "scale(3)"]))

(make-frames!
  "pulse"
  [0 25 50 75 100]
  (make-body "transform" ["scale(1.3)"
                          "scale(1.3)"
                          "scale(1.3)"
                          "scale(1)"
                          "scale(1.3)"]))

(make-frames!
  "pulse2"
  [0 25 50 75 100]
  (make-body "transform" ["scale(1)"
                          "scale(1.075)"
                          "scale(1.15)"
                          "scale(1.225)"
                          "scale(1.3)"]))

(a-to-b! "new-fi" "fill-opacity" "0" ".5")

(a-to-b! "sc-rot"
         "transform"
         "scale(4) rotate(0deg)"
         "scale(30) rotate(-80deg)")

(a-to-b! "grow2to3"
         "transform"
         "rotate(280deg) scale(1)"
         "rotate(280deg) scale(1.2)")

(make-frames!
  "woosh"
  [10 35 55 85 92]
  (make-body "transform" ["translate(80vw, 50vh) rotate(2deg) scale(1.2)"
                          "translate(60vw, 60vh) rotate(-200deg) scale(2.4)"
                          "translate(40vw, 40vh) rotate(120deg) scale(4.4)"
                          "translate(20vw, 30vh) rotate(-1000deg) scale(3.2)"
                          "translate(60vw, 80vh) rotate(300deg) scale(6.2)"]))

(make-frames!
  "woosh-2"
  [10, 35, 55, 85, 92]
  (make-body "transform" ["translate(80vw, 50vh) rotate(2deg) scale(11.2)"
                          "translate(60vw, 60vh) rotate(-200deg) scale(12.4)"
                          "translate(40vw, 40vh) rotate(120deg) scale(13.4)"
                          "translate(20vw, 30vh) rotate(-210deg) scale(12.2)"
                          "translate(60vw, 80vh) rotate(400deg) scale(6.2)"]))

(make-frames!
  "woosh-3"
  [10 55 85 92]
  (make-body "transform" ["translate(60vw, 10vh) rotate(2deg) scale(2.2)"
                          "translate(40vw, 40vh) rotate(120deg) scale(8.4)"
                          "translate(50vw, 30vh) rotate(0deg) scale(12.2)"
                          "translate(60vw, 80vh) rotate(400deg) scale(4.2)"]))
(make-frames!
  "woosh-4"
  [10, 35, 55, 85, 92]
  (make-body "transform" ["translate(80vw, 10vh) rotate(2deg) scale(2.2)"
                          "translate(40vw, 40vh) rotate(220deg) scale(10.4)"
                          "translate(50vw, 30vh) rotate(0deg) scale(4.2)"
                          "translate(50vw, 30vh) rotate(-300deg) scale(2.2)"
                          "translate(60vw, 80vh) rotate(400deg) scale(1.2)"]))

(make-frames!
  "woosh-5"
  [0 12 25 38 50 62 75 88 100]
  (make-body "transform" ["translate(10vw, 60vh) rotate(2deg) scale(4.4)"
                          "translate(45vw, 45vh) rotate(-200deg) scale(0.4)"
                          "translate(80vw, 80vh) rotate(100deg) scale(2.8)"
                          "translate(60vw, 20vh) rotate(-300deg) scale(2.6)"
                          "translate(20vw, 40vh) rotate(45deg) scale(4)"
                          "translate(40vw, 10vh) rotate(-120deg) scale(1)"
                          "translate(80vw, 32vh) rotate(90deg) scale(1.2)"
                          "translate(42vw, 77vh) rotate(180deg) scale(1.5)"
                          "translate(10vw, 60vh) rotate(2deg) scale(3.8)"]))

(make-frames!
  "woosh-6"
  [0 12 25 38 50 62 75 88 100]
  (make-body "transform" ["translate(10vw, 60vh) rotate(2deg) scale(3.8)"
                          "translate(45vw, 45vh) rotate(-200deg) scale(0.8)"
                          "translate(80vw, 80vh) rotate(100deg) scale(2.4)"
                          "translate(60vw, 20vh) rotate(-300deg) scale(0)"
                          "translate(20vw, 40vh) rotate(45deg) scale(2.6)"
                          "translate(40vw, 10vh) rotate(-120deg) scale(0.2)"
                          "translate(80vw, 32vh) rotate(90deg) scale(2.8)"
                          "translate(42vw, 77vh) rotate(180deg) scale(0.6)"
                          "translate(10vw, 60vh) rotate(2deg) scale(3.8)"]))

(make-frames!
  "woosh-7"
  [20, 40, 60, 80, 100]
  (make-body "transform" ["translate(20vw, 80vh) rotate(20deg) scale(0.4)"
                          "translate(55vw, 60vh) rotate(-200deg) scale(1.6)"
                          "translate(70vw, 70vh) rotate(0deg) scale(4.2)"
                          "translate(80vw, 45vh) rotate(-300deg) scale(2.2)"
                          "translate(10vw, 50vh) rotate(0deg) scale(2.2)"]))

(make-frames!
  "woosh-8"
  [20, 40, 60, 80, 100]
  (make-body "transform" ["translate(20vw, 80vh) rotate(2deg) scale(0.8)"
                          "translate(55vw, 60vh) rotate(-200deg) scale(2)"
                          "translate(70vw, 70vh) rotate(0deg) scale(4.4)"
                          "translate(80vw, 45vh) rotate(-300deg) scale(2.2)"
                          "translate(10vw, 50vh) rotate(0deg) scale(2.2)"]))

(make-frames!
  "spiral1"
  [0 50 100]
  (make-body "transform" ["rotate(0deg) translate(10vw, 30vh) rotate(0deg)"
                          "rotate(360deg) translate(0px) rotate(-360deg)"
                          "rotate(0deg) translate(10vw, 30vh) rotate(0deg)"]))

(make-frames!
  "spiral2"
  [0 50 100]
  (make-body "transform" ["rotate(120deg) translate(10vw, 30vh) rotate(-120deg)"
                          "rotate(480deg) translate(0px) rotate(-480deg)"
                          "rotate(120deg) translate(10vw, 30vh) rotate(-120deg)"]))

(make-frames!
  "spiral3"
  [0 50 100]
  (make-body "transform" ["rotate(240deg) translate(10vw, 30vh) rotate(-240deg)"
                          "rotate(600deg) translate(0px) rotate(-600deg)"
                          "rotate(240deg) translate(10vw, 30vh) rotate(-240deg)"]))

(make-frames!
  "spiral4"
  [0 50 100]
  (make-body "transform" ["rotate(60deg) translate(10vw, 30vh) rotate(-60deg)"
                          "rotate(420deg) translate(0px) rotate(-420deg)"
                          "rotate(60deg) translate(10vw, 30vh) rotate(-60deg)"]))

(make-frames!
  "spiral5"
  [0 50 100]
  (make-body "transform" ["rotate(180deg) translate(10vw, 30vh) rotate(-180deg)"
                          "rotate(540deg) translate(0px) rotate(-540deg)"
                          "rotate(180deg) translate(10vw, 30vh) rotate(-180deg)"]))

(make-frames!
  "spiral6"
  [0 50 100]
  (make-body "transform" ["rotate(300deg) translate(10vw, 30vh) rotate(-300deg)"
                          "rotate(660deg) translate(0px) rotate(-660deg)"
                          "rotate(300deg) translate(10vw, 30vh) rotate(-300deg)"]))

(defn transform-values [[x y scale rotation]]
  (str "translate(" x "vw, " y "vh) scale(" scale ") rotate(" rotation "deg)"))

(make-frames!
  "move-across2"
  [0 25 50 75 100]
  (make-body "transform" (map transform-values [[50 75 1 0]
                                                [75 50 1 0]
                                                [50 25 1 0]
                                                [25 50 1 0]
                                                [50 75 1 0]])))

(make-frames!
  "zig-zag"
  [0 25 50 75 100]
  (make-body "transform" (map transform-values [[-15 -15 2 0]
                                                [25 25 2 90]
                                                [-15 50 2 180]
                                                [25 75 2 270]
                                                [-15 100 2 360]])))

(make-frames!
  "loopy-left"
  [10, 35, 55, 85, 92]
  (make-body "transform" ["translate(90vw, 10vh) rotate(2deg) scale(2.2)"
                          "translate(80vw, 30vh) rotate(220deg) scale(6.4)"
                          "translate(60vw, 40vh) rotate(0deg) scale(4.2)"
                          "translate(30vw, 80vh) rotate(-300deg) scale(2.2)"
                          "translate(10vw, 90vh) rotate(400deg) scale(3.2)"]))

(make-frames!
  "loopy-right"
  [10, 35, 55, 85, 92]
  (make-body "transform" ["translate(10vw, 10vh) rotate(2deg) scale(2.2)"
                          "translate(30vw, 80vh) rotate(220deg) scale(6.4)"
                          "translate(60vw, 40vh) rotate(0deg) scale(4.2)"
                          "translate(80vw, 30vh) rotate(-300deg) scale(2.2)"
                          "translate(90vw, 90vh) rotate(400deg) scale(3.2)"]))

(make-frames!
  "dashy"
  [100]
  (make-body "stroke-dashoffset" [0]))


;; ------------------- BLINKING ---------------------------

(defn frame-mod [val n frame] (= val (mod frame n)))
(def nth-frame (partial frame-mod 0))
(defn even-frame [frame] (nth-frame 2 frame))
(defn odd-frame [frame] (frame-mod 1 2 frame))

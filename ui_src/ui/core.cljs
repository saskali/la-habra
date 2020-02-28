(ns ui.core
  (:require [clojure.string  :refer [split-lines split join]]
            [reagent.core :as r]
            [ui.helpers :refer [cos sin style url val-cyc slow-val-cyc]]
            [ui.shapes :refer [tri square pent hex hept oct
                               b1 b2 b3 b4
                               semi-circle]]
            [ui.fills :refer [gray mint navy blue midnight orange pink white yellow
                              earth-colors water-colors blue-set-2 color-set-2]]
            [ui.generators :refer [freak-out new-freakout freak-out-waves scatter lerp draw
                                   gen-circ gen-line gen-poly gen-rect gen-shape gen-half-circ
                                   gen-group gen-offset-lines gen-bg-lines gen-mask
                                   gen-grid gen-line-grid gen-cols gen-rows
                                   get-scaling-factors]]
            [ui.filters :refer [turb noiz soft-noiz disappearing splotchy blur]]
            [ui.patterns :refer [gen-color-noise pattern pattern-def
                                 blue-dots blue-lines
                                 pink-dots pink-lines pink-dots-1 pink-dots-2 pink-dots-3 pink-dots-4 pink-dots-5
                                 gray-dots gray-dots-lg gray-lines gray-patch
                                 mint-dots mint-lines
                                 navy-dots navy-lines
                                 orange-dots orange-lines
                                 br-orange-dots br-orange-lines
                                 yellow-dots yellow-lines
                                 white-dots white-dots-lg white-lines
                                 shadow noise]]
            [ui.animations :refer [make-body splice-bodies make-frames!
                                   nth-frame even-frame odd-frame
                                   seconds-to-frames frames-to-seconds
                                   anim anim-and-hold a-to-b!]]))


(enable-console-print!)

(println "Loaded.")

(defn hide-display []
  (let [heads-up-display (.getElementById js/document "figwheel-heads-up-container")]
    (.setAttribute heads-up-display "style" "display: none")))


;; ------------------------ SETTINGS  ---------------------

;(hide-display) ;; hides heads up display for performance

(defn bpm->ms [bpm]
  (quot 60000 bpm))

(def bpm (r/atom 90))
(def ms (-> @bpm bpm->ms (/ 1000) r/atom))

(def width (r/atom 1920)) ;;(atom (.-innerWidth js/window)))
(def height (r/atom 1080)) ;;(atom (.-innerHeight js/window)))

(def settings {:width @width
               :height @height})


;; ----------- ANIMATIONS ----------------

;; syntax reminder
; (make-frames!
;   "NAME"
;   [frames]
;   (make-body "ATTRIBUTE" [values]))

; (trans x y)
; (nth-frame num FRAME)
; (even-frame FRAME)
; (odd-frame FRAME)

; "fade-in-out" "fade-out" "fade-in" "wee-oo" "rot" "cent-rot" "rev"


;; --------------- ANIMATIONS STORAGE --------------------

(a-to-b! "bbll" "fill" pink white)

(a-to-b! "slide-up"
         "transform"
         "translateY(125%)"
         (str "translateY("(* 0.15 @height)")"))

(make-frames!
  "supercolor"
  [10 35 55 85 92]
  (make-body "fill" [pink pink yellow midnight midnight]))

(make-frames!
  "colorcolor"
  [10 35 55 85 92]
  (make-body "fill" (mapv #(pattern (str "noise-" %))
                          [mint mint mint orange orange])))

(make-frames!
  "colorcolorcolor"
  [10 35 55 85 92]
  (make-body "fill" (mapv #(pattern (str "noise-" %))
                          [navy mint orange pink gray])))

(make-frames!
  "earthcolors"
  [0 25 50 75 100]
  (let [{:keys [dark-purple purple light-purple]} earth-colors]
    (make-body "fill" [dark-purple purple light-purple purple dark-purple])))

(make-frames!
  "morph"
  [0 15 30 45 60 75 100]
  (make-body "d" [(str "path('"tri"')")
                  (str "path('"square"')")
                  (str "path('"pent"')")
                  (str "path('"hex"')")
                  (str "path('"hept"')")
                  (str "path('"oct"')")
                  (str "path('"tri"')")]))

(make-frames!
  "morph2"
  [0 33 66 100]
  (make-body "d" [(str "path('"pent"')")
                  (str "path('"hept"')")
                  (str "path('"oct"')")
                  (str "path('"pent"')")]))


;; --------------- ATOMS STORAGE --------------------

(def seed
  (->> (gen-circ (get earth-colors :gold)
                 (quot @width 2)
                 -40
                 30)
       (anim "etof" 200 @ms "1")
       (draw)
       (r/atom)))

(def earth
  (->> (gen-circ (get earth-colors :blue)
                 (* 0.5 @width)
                 (* 1.4 @height)
                 560)
       ;(anim "pulse" 32 @ms "2")
       (anim "pulse2" 32 @ms "infinite")
       (draw)
       (r/atom)))

(def expanding-circle
  (->> (gen-circ (get earth-colors :dark-purple)
                 (* 0.5 @width)
                 (* 1.4 @height)
                 600)
       (style {:animation (str "earthcolors "
                               (* 16 @ms)
                               "s linear infinite, "
                               "scaley-up "
                               (* 16 @ms)
                               "s linear infinite")})
               ;:transform-origin "center" :transform "scale(3)"})
       (draw)
       (r/atom)))

(def drops
  (-> (fn [delay]
        (->> (gen-rect gray (+ 30 (* delay 160)) 5 200 36)
             (anim "etof" 2 @ms "infinite" {:delay (str (* 0.5 delay) "s")})
             (draw)))
      (map (range 20))
      (r/atom)))

(def drops2
  (-> (fn [delay]
        (->> (gen-circ white (+ 30 (* delay 160)) -10 10)
             (anim "etof2" 2 @ms "infinite" {:delay (str (* 0.25 delay) "s")})
             (draw)))
      (map (range 10))
      (r/atom)))

(def spiral-ball1
  (->> (gen-circ (get water-colors :blue) (* 0.5 @width) (* 0.5 @height) 20)
       (anim "spiral1" (-> (rand-int 5) (+ 4)) @ms "infinite" {:timing "linear"})
       (draw)
       (r/atom)))

(def spiral-ball2
  (->> (gen-circ (get water-colors :light-orange) (* 0.5 @width) (* 0.5 @height) 30)
       (anim "spiral2" (-> (rand-int 5) (+ 4)) @ms "infinite" {:timing "linear"})
       (draw)
       (r/atom)))

(def spiral-ball3
  (->> (gen-circ (get water-colors :orange) (* 0.5 @width) (* 0.5 @height) 40)
       (anim "spiral3" (-> (rand-int 5) (+ 4)) @ms "infinite" {:timing "linear"})
       (draw)
       (r/atom)))

(def spiral-ball4
  (->> (gen-circ (get water-colors :blue) (* 0.5 @width) (* 0.5 @height) 50)
       (anim "spiral4" (-> (rand-int 5) (+ 4)) @ms "infinite" {:timing "linear"})
       (draw)
       (r/atom)))

(def spiral-ball5
  (->> (gen-circ (get water-colors :light-orange) (* 0.5 @width) (* 0.5 @height) 60)
       (anim "spiral5" (-> (rand-int 5) (+ 4)) @ms "infinite" {:timing "linear"})
       (draw)
       (r/atom)))

(def spiral-ball6
  (->> (gen-circ (get water-colors :orange) (* 0.5 @width) (* 0.5 @height) 70)
       (anim "spiral6" (-> (rand-int 5) (+ 4)) @ms "infinite" {:timing "linear"})
       (draw)
       (r/atom)))

(defn frame-flimmer-shapes []
  (let [left? (rand-int 2)
        up? (rand-int 2)
        shift (rand-int 11)
        x (if (zero? left?)
            (+ 10 shift)
            (- 80 shift))
        y (if (zero? up?)
            (+ 10 shift)
            (- 75 shift))]
    (->> (gen-shape (:dark-dark-blue water-colors) (rand-nth [hex hept oct]))
         (style {:opacity 0.8
                 :transform (str "translate("
                                 x
                                 "vw, "
                                 y
                                 "vh) rotate(45deg)")})
         (draw)
         (r/atom))))

(def hept1-white-dots-anim
  (->> (gen-shape (pattern (:id white-dots)) hept)
       (style {:transform-origin "center" :transform "scale(1.6)"})
       (anim "woosh-6" 4 @ms "infinite")
       (draw)
       (r/atom)))

(def hept1-pink-anim
  (->> (gen-shape (pattern pink) hept)
       (style {:mix-blend-mode "difference"})
       (anim "woosh-5" 4 @ms "infinite")
       (draw)
       (r/atom)))

(def hept1-mint-anim
  (->> (gen-shape mint hept)
       (style {:mix-blend-mode "difference"
               :transform-origin "center"
               :transform "scale(1.8)"})
       (anim "woosh-6" 4 @ms "infinite")
       (draw)
       (r/atom)))

(def hept2-white-dots-anim
  (->> (gen-shape (pattern (:id white-dots)) hept)
       (style {:transform-origin "center" :transform "scale(1.4)"})
       (anim "woosh-7" 4 @ms "infinite")
       (draw)
       (r/atom)))

(def hept2-mint-anim
  (->> (gen-shape mint hept)
       (style {:mix-blend-mode "difference"})
       (anim "woosh-8" 4 @ms "infinite")
       (draw)
       (r/atom)))

(def hept2-pink-anim
  (->> (gen-shape (pattern pink) hept)
       (style {:mix-blend-mode "difference"
               :transform-origin "center"
               :transform "scale(1.8)"})
       (anim "woosh-8" 8 @ms "infinite")
       (draw)
       (r/atom)))

(def hept3-orange-anim
  (->> (gen-shape orange hept)
       (style {:transform "translate(30vw, 44vh) scale(2.4)"})
       (style {:mix-blend-mode "color-burn"})
       (anim "woosh-3" 6 @ms "infinite")
       (draw)
       (r/atom)))

(def noise-circ-anim ;; use with orange background
  (->> (gen-circ (pattern (str "noise-" white))
                 (* 0.5 @width)
                 (* 0.5 @height)
                 100)
       (style {:mix-blend-mode "color-dodge"
               :opacity 0.4
               :transform-origin "center"
               :transform "scale(4)"})
       (anim "sc-rot" 40 @ms "infinite" {:timing "linear"})
       (draw)
       (r/atom)))

(def oct-grid-anim
  (->> (gen-grid 4 4
                 {:col 20 :row 20}
                 (gen-shape mint oct))
       (map #(style {:mix-blend-mode "difference"}  %))
       (map #(anim "supercolor" (rand-int 100) @ms "infinite" %))
       (map draw)
       (map #(gen-group {:style {:transform-origin "center"
                                 :transform (str "rotate(" (rand-int 360) "deg)"
                                                 "scale(60) translate(-20vh, -20vh)")}} %))
       (r/atom)))

(def shimmer-anim
  (->> (gen-grid 86 42
                 {:col 20 :row 20}
                 (gen-shape mint oct))
       (map #(style {:mix-blend-mode "difference"}  %))
       (map #(anim "supercolor" (rand-int 100) @ms "infinite" %))
       (map draw)
       (r/atom)))

(def pixie-dust-anim
  (->> (gen-grid 86 42
                 {:col 40 :row 40}
                 (gen-shape mint oct))
       (map #(style {:mix-blend-mode "difference"
                     :opacity 0.1}  %))
       (map #(anim "supercolor" (rand-int 100) @ms "infinite" %))
       (map draw)
       (map #(gen-group {:style {:transform-origin "center"
                                 :transform (str "rotate(" (rand-int 360) "deg)"
                                                 "scale(1.2) translate(-20vh, -20vh)")}} %))
       (r/atom)))

(def shape-shift-anim
  (->> (gen-shape mint oct)
       (style {:transform "translate(10vw, 30vh) scale(2) rotate(45deg)"})
       (style {:mix-blend-mode "difference" :filter (url (:id noiz))})
       ;(style {:mix-blend-mode "difference"})
       (anim "morph" 16 @ms "infinite")
       (draw)
       (r/atom)))

(def bb4
  (->> (gen-shape yellow oct)
       (style {:transform "translate(10vw, 30vh) scale(2) rotate(45deg)"})
       ;(style {:mix-blend-mode "color-dodge" :filter (url (:id noiz))} )
       (style {:mix-blend-mode "color-dodge"})
       (anim "woosh" 4 @ms "infinite")
       (draw)
       (r/atom)))

(def bb5
  (->> (gen-shape mint oct)
       (style {:transform "translate(10vw, 30vh) scale(2) rotate(45deg)"
               :animation "woosh-3 10.656s linear infinite"
               :mix-blend-mode "luminosity"})
       (draw)
       (r/atom)))

(def bb6s
  (->> (gen-shape (pattern (:id pink-dots)) oct)
       (style {:transform "translate(10vw, 30vh) scale(2.4)"})
       (style {:mix-blend-mode "color-burn"})
       (anim "woosh-3" 6 @ms "infinite")
       (draw)
       (r/atom)))

(def scale-me
  (->> (gen-rect (pattern (str "noise-" yellow)) 0 0 @width @height)
       (style {:transform "scale(50)"})
       (anim "scaley-huge" 10 @ms "infinite")
       (draw)
       (r/atom)))

(def new-scale
  (->> (gen-circ white 0 0 100)
       (style {:opacity 0.7})
       (style {:transform "translate(10vh, 60vh)"})
       (gen-group {:style {:animation "scaley 10s infinite"}})
       (r/atom)))

(def sc-circ
  (->> (gen-circ (pattern (:id orange-lines))
                 (* 0.5 @width)
                 (* 0.4 @height)
                 100)
       (anim "scaley" 10 @ms "infinite")
       (draw)
       (r/atom)))

(def mf
  (->> (gen-shape white tri)
       (anim "morph" 12 @ms "infinite")
       (style {:opacity 0.7
               :mix-blend-mode "difference"})
       (draw)
       (gen-group {:style {:transform-origin "center"
                           :transform "translate(-2vw, 10vh) scale(4)"}})
       (r/atom)))

(defn moving-triangle [frame color]
  (->> (gen-shape color tri)
       (style {:transform-origin "center"
               :transform (str "translate(40vw, 40vh) rotate("
                               (val-cyc frame [85 85 85 85 245 245 245 245 -80 -80 -80 -80])
                               "deg) scale(4)")})
       (style {:mix-blend-mode "overlay"})
       (draw)
       (when (nth-frame 2 frame))
       (r/atom)))

(def POLYGON
  (->>
    (gen-poly (:aquamarine blue-set-2) [100 100 400 400 300 100 200 50])
    (style {:opacity 0.7})
    (anim "woosh" 20 @ms "infinite")
    (draw)
    (r/atom)))

(def rr
  (->> (gen-grid 20
                 30
                 {:col 100 :row 150}
                 (->> (gen-shape mint tri)))
       ;(map #(style styles %))
       ; map #(anim "rot" 20 @ms "infinite" %))
       (map draw)
       (map #(gen-group {:style {:transform-origin "center"
                                 :transform "scale(2)"}} %))
       (map #(gen-group {:mask (url "bitey")
                         :style {:transform-origin "center"
                                 :animation "rot 10s infinite"}} %))
       (r/atom)))

(def rr2
  (->> (gen-grid 20 30
                 {:col 100 :row 150}
                 (->> (gen-shape yellow tri)))
       (map #(style {:opacity 1 :mix-blend-mode "difference"} %))
       (map #(anim "morph" 10 @ms "infinite" %))
       (map draw)
       (map #(gen-group {:style {:transform-origin "center"
                                 :transform "scale(2)"}} %))
       (map #(gen-group {:style {:transform-origin "center"
                                 :transform "translate(-200px, -100px)"}} %))
       (map #(gen-group {:style {:transform-origin "center"
                                 :animation "rot 5s infinite"}}))
       (r/atom)))

(def spinning-triangles-pink
  (r/atom (->> (gen-grid 3
                         2
                         {:col 400 :row 300}
                         (->> (gen-shape pink tri)))
               ;(map #(style styles %))
               ;(map #(anim "rot" 20 @ms "infinite" %))
               (map draw)
               (map #(gen-group {:style {:transform-origin "center"
                                         :transform "translate(0px, 50px) scale(2)"}} %))
               (map #(gen-group {:mask (url "bitey")
                                 :style {:transform-origin "center"
                                         :animation "smooth-rot 1s linear infinite"}} %)))))

(def spinning-triangles-pink2
  (r/atom (->> (gen-grid 20 30
                         {:col 100 :row 150}
                         (->> (gen-shape yellow tri)))
               (map #(style {:opacity 1 :mix-blend-mode "difference"} %))
               (map #(anim "morph" 10 @ms "infinite" %))
               (map draw)
               (map #(gen-group {:style {:transform-origin "center"
                                         :transform "scale(2)"}} %))
               (map #(gen-group {:style {:transform-origin "center"
                                         :transform "translate(-200px, -100px)"}} %))
               (map #(gen-group {:style {:transform-origin "center"
                                         :animation "rot 5s infinite"}})))))

(def spinning-triangles-mint
  (r/atom (->> (gen-grid 30
                         1
                         {:col 60 :row 180}
                         (->> (gen-shape mint tri)))
               (map draw)
               (map #(gen-group {:style {:mix-blend-mode "difference"
                                         :transform-origin "center"
                                         :animation (str "zig-zag "
                                                         (* 4 @ms)
                                                         "s linear infinite")}} %)))))

;;;;;;;;;;;;;;
;; SPLASHES ;;
;;;;;;;;;;;;;;

(defonce splash-g (scatter 20 (->> (gen-circ gray 10 10 20)
                                   (style {:opacity 0.4})
                                   (draw))))

(defonce splash-y (scatter 20 (->> (gen-circ yellow 10 10 20)
                                   (style {:opacity 0.4})
                                   (draw))))

(defonce splash-yl (scatter 10 (->> (gen-circ yellow 10 10 60)
                                    (style {:mix-blend-mode "screen"})
                                    (draw))))

(defonce splash-ylo (scatter 10 (->> (gen-circ yellow 10 10 60)
                                     (style {:mix-blend-mode "overlay"})
                                     (draw))))

(defonce splash-plm (scatter 10 (->> (gen-circ pink 10 10 60)
                                     (style {:mix-blend-mode "multiply"})
                                     (draw))))


;; --------------- SHAPES STORAGE --------------------

(defn gen-moving-rects-hz [frame speed partitions]
  (let [scales (get-scaling-factors speed partitions)
        largest-scaling-factor (last scales)]
    (->> (gen-rect mint 0 0 (quot @width largest-scaling-factor) @height)
         (style {:mix-blend-mode "difference"
                 :opacity 0.6})
         (style {:transform (str "scaleX("
                                 (val-cyc frame scales)
                                 ")")})
         (draw)
         (when (nth-frame 1 frame)))))

(defn gen-moving-rects-vt [frame speed partitions]
  (let [scales (get-scaling-factors speed partitions)
        largest-scaling-factor (last scales)]
    (->> (gen-rect pink 0 0 @width (quot @height largest-scaling-factor))
         (style {:mix-blend-mode "difference"
                 :opacity 0.6})
         (style {:transform (str "scaleY("
                                 (val-cyc frame scales)
                                 ")")})
         (draw)
         (when (nth-frame 1 frame)))))

(defn sun-shape [frame color base-x base-y style s cycle]
  (map (fn [[x y r]]
         (let [r (if (and (= 960 base-x) (= 200 base-y))
                   (+ 4 r)
                   (-> (* 6 s) (+ r) (- 3)))]
           (->> (gen-circ color x y r)
                (style style)
                (draw))))
       (concat (take cycle [[base-x (- base-y 80) 18]
                            [(+ base-x 50) (- base-y 50) 14]
                            [(+ 80 base-x) base-y 18]
                            [(+ base-x 50) (+ base-y 55) 14]
                            [base-x (+ 80 base-y) 18]
                            [(- base-x 55) (+ base-y 55) 14]
                            [(- base-x 80) base-y 18]
                            [(- base-x 55) (- base-y 50) 14]])
               (when (nth-frame 31 frame) [[base-x base-y 40]]))))


;; ------------------- DRAWING HELPERS ------------------------

;; use with (doall (map fn range))
(defn thin
  [color frame flicker? n]
  (let [op (if (and (nth-frame 4 frame) flicker?) (rand) 1)]
    (->>
     (gen-rect color (* 0.15 @width) (* 0.15 @height) (* 0.7 @width) 3)
     (style {:transform (str "translateY(" (* n 10) "px)") :opacity op})
     (draw))))

(defn flicker-test [n frame]
  (or (and (= n 10) (nth-frame 12 frame))
      (and (= n 12) (nth-frame 8 frame))
      (= n 44) (= n 45)
      (and (= n 46) (nth-frame 8 frame))))

;(doall (map deref levels))
(def levels
  (map-indexed
    (fn [idx color]
        (->>
          (gen-rect color -100 -100 "120%" "120%" (url "cutout"))
          (style {:opacity 0.4
                  :transform-origin "center"
                  :transform (str
                              "translate(" (- (rand-int 200) 100) "px, " (- (rand-int 300) 100) "px)"
                              "rotate(" (- 360 (rand-int 720)) "deg)")})
          (anim "fade-in-out" "10s" "infinite" {:delay (str (* 0.1 idx) "s")})
          (draw)
          (r/atom)))
    (take 10 (repeatedly #(nth [mint navy navy mint] (rand-int 6))))))


 ;; ----------- COLLECTION SETUP AND CHANGE ----------------

(def DEBUG false)

(when-not DEBUG
  (defonce collection (r/atom (list))))

;(reset! ran {})
;(def l1 (lerp))
;(defn cx2 [frame])

(defn cx [frame]
  (let
    [colors [(:dark-blue water-colors)];(slow-val-cyc 16 (->> earth-colors vals (take 4) vec)) (get earth-colors :dark-purple)
            ;(slow-val-cyc 12 [midnight]) (:dark-blue water-colors)
            ;(slow-val-cyc 12 [midnight])
            ;(slow-val-cyc 12 [midnight])
            ;(slow-val-cyc 12 [midnight])
     {:keys [dark-green aquamarine brown-red orange-red]} blue-set-2
     s (->> (quot frame 4) (.sin js/Math) (.abs js/Math))]

    (list
      ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
      ;;;;;;;;;;;;;;;;;; BACKGROUNDS ;;;;;;;;;;;;;;;;;;;;;;;;
      ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
      (->> (gen-rect (val-cyc frame colors) 0 0 "100vw" "100%")
           ;(style {:opacity 0.9})
           (draw))

      ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
      ;;;;;;;;;;;;;;;;;; PATTERNS ;;;;;;;;;;;;;;;;;;;;;;;;;;;
      ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

      ;;;;;;;;;;;;;;;;;;;;;;;;;;;
      ;;;;;;;;;; EARTH ;;;;;;;;;;
      ;;;;;;;;;;;;;;;;;;;;;;;;;;;

      ;; missing sense for resolution
      ;; there doesn't seem to be a climax
      ;; keep big background ball up
      ;; make small structures collapse in themselves and slowly disappear
      ;; collapse big ball on the bottom

      ;;;;;;;;;;;;;;
      ;;; GROUND ;;;
      ;;;;;;;;;;;;;;
      ;@expanding-circle
      ;@seed
      ;@earth
      ;;;;;;;;;;;;;;;;;;
      ;;; SUN-SHAPES ;;;
      ;;;;;;;;;;;;;;;;;;
      ;(map (fn [[x y]]
      ;       (sun-shape frame midnight x y {} s (val-cyc frame (->> (range 1 9) (slow-val-cyc 4)))))
      ;     [[360 200] [660 300] [960 200] [1260 300] [1560 200]
      ;      [960 500] [460 500] [1460 500]
      ;      [700 700] [1200 700]])
      ;
      ;(->> (gen-half-circ midnight 916 200)
      ;     (draw)))))

      ;;;;;;;;;;;;;;;;;;;;;;;;;;;
      ;;;;;;;;;; WATER ;;;;;;;;;;
      ;;;;;;;;;;;;;;;;;;;;;;;;;;;

      ;; TODOS
      ;; bring in more fluid movements
      ;; rotate drops
      ;; too many straight lines -> more round things
      ;; sine waves instead of horizontal waves
      ;; something like raindrops
      ;; some shape moving from outside of screen in sine waves to other side
      ;; waves - ripple effect on the water
      ;; drops are good
      ;; more random drops
      ;; grid of dots feels out of place -> too predictable -> maybe random -> colors of the balls are orange
      ;; blur noise circ anim -> go forwards and backwards for continuity
      ;; bb5 is too predictable kind of repeatedly too sharp -> random movement
      ;
      ;(gen-bg-lines (:blue water-colors)
      ;              (mod frame 70)
      ;              {:opacity 0.4})
      ;
      ;(when (< 3 (mod frame 8))
      ;  (freak-out 1920 1080 10 100 (get water-colors :orange)))
      ;
      ;(when (> (mod frame 16) 4)
      ;  @drops2)
      ;
      ;(when (nth-frame 4 frame)
      ;  @(frame-flimmer-shapes))
      ;
      ;@spiral-ball1
      ;@spiral-ball2
      ;@spiral-ball3
      ;@spiral-ball4
      ;@spiral-ball5
      ;@spiral-ball6)))

      ;;; (style {:filter (url (:id turb))}
      ;
      ;@bb5)))
      ;
      ;@noise-circ-anim)))



      ;(gen-cols brown-red (* 6 (mod frame 10)) 60 60)
      ;
      ;(->> (gen-grid 20 20
      ;               {:col 100 :row 100}
      ;               (gen-circ orange-red 30 30 (-> (* 10 s) (+ 6))))
      ;     (map #(style {:opacity 0.7} %))
      ;     (map draw)))))

      ;(gen-moving-rects-vt frame 2 10)
      ;(gen-moving-rects-hz frame 2 10))))


      ;;;;;;;;;;;;;;;;;;;;;;;;;;;
      ;;;;;;;;;; FIRE ;;;;;;;;;;;
      ;;;;;;;;;;;;;;;;;;;;;;;;;;;

      ;; TODOS
      ;; might keep some elements between phases
      ;; first thing that gets in musically: congas
      ;; things flashing: dots scattered randomly - blink - shrink - enlarge
      ;; build up with kick - sync something - circle
      ;; heptagon too predictable - more chaotic - animate at random positions)))
      ;; transition to fire
      ;; like explosion of colors -> more red though
      ;; shape that moves too predictable -> get in old hept merging thing
      ;
      @oct-grid-anim

      (when (or (= 0 (mod frame 4))
                (= 2 (mod frame 4)))
        (new-freakout @width @height 1 50 "mypent"))

      @spinning-triangles-pink
      @spinning-triangles-mint

      ;@bg)))
      ;@bb)))

      ;;;;;;;;;;;;;;;;;;;;;;;;;;;
      ;;;;; MOVING HEPTAGONS ;;;;
      ;;;;;;;;;;;;;;;;;;;;;;;;;;;
      @hept1-mint-anim
      @hept1-pink-anim
      @hept1-white-dots-anim)))

      ;@anim-hept2-pink)))
      ;@anim-hept2-mint
      ;@anim-hept2-white-dots
      ;
      ;;;;;;;;;;;;;;;;;;;;;;;;;;;
      ;;;; OVERLAY RECTANGLES ;;;
      ;;;;;;;;;;;;;;;;;;;;;;;;;;;
      ;(->> (gen-rect mint 0 0 400 600)
      ;     (style {:mix-blend-mode "difference"
      ;             :opacity 0.6})
      ;     (style {:transform (str "scale("
      ;                             (val-cyc frame (concat (repeat 3 4)
      ;                                                    (repeat 3 6)
      ;                                                    (repeat 3 8)
      ;                                                    (repeat 3 20)))
      ;                             ")")})
      ;     (draw)
      ;     (when (nth-frame 1 frame)))
      ;
      ;(->> (gen-rect pink 0 0 400 600)
      ;     (style {:mix-blend-mode "difference"
      ;             :opacity 0.6})
      ;     (style {:transform (str "scale("
      ;                             (val-cyc frame (concat (repeat 3 20)
      ;                                                    (repeat 3 8)
      ;                                                    (repeat 3 6)
      ;                                                    (repeat 3 4)))
      ;                             ")")})
      ;     (draw)
      ;     (when (nth-frame 1 frame))))))

      ;;;;;;;;;;;;;;;
      ;;;; SPLASH ;;;
      ;;;;;;;;;;;;;;;
      ;(when (and (nth-frame 4 frame)
      ;           (nth-frame 3 frame)) @splash-yellow)
      ;
      ;(when (nth-frame 5 frame) @splash-gray)
      ;
      ;;;;;;;;;;;;;
      ;;;; GRID ;;;
      ;;;;;;;;;;;;;
      ;(when (nth-frame 4 frame)
      ;  (gen-line-grid pink 6 80 80
      ;                 {:col 200 :row 200})))))

      ;;;;;;;;;;;;;;;;;;;;;;;;
      ;;;; FREAKOUT CIRCLE ;;;
      ;;;;;;;;;;;;;;;;;;;;;;;;
      ;(when (nth-frame 3 frame)
      ;  (new-freakout @width @height 2 50 "testCirc3")))))


      ;;;;;;;;;;;;;;;;;;;;;;;;;;;
      ;;;;;;;;;;; AIR ;;;;;;;;;;;
      ;;;;;;;;;;;;;;;;;;;;;;;;;;;

      ;; TODOS
      ;; gradient seemless transitions
      ;; maybe circles are moving too fast
      ;; pick different colors
      ;; fill the screen even more
      ;; gpu not happy with huge amount of objects -> use blur
      ;; small balls are good
      ;; airy colors -> lots of blues
      ;; more variation
      ;; come up with build up
      ;
      ;
      ;;;;;;;;;;;;;;;;;;;;;;;;;;;
      ;;;; OVERLAY RECTANGLES ;;;
      ;;;;;;;;;;;;;;;;;;;;;;;;;;;
      ;(gen-moving-rects-hz frame 8 8)
      ;(gen-moving-rects-vt frame 8 8)
      ;
      ;;;;;;;;;;;;;;;;;
      ;;;; FREAKOUT ;;;
      ;;;;;;;;;;;;;;;;;
      ;(freak-out-waves s aquamarine)
      ;(new-freakout @width
      ;              @height
      ;              3
      ;              (->> (slow-val-cyc 2 [100 50 25 50 100])
      ;                   (val-cyc frame))
      ;              "testCirc2"))))


      ;;;;;;;;;;;;;;;;;;;;;;;;;;;
      ;;;;;;;;;; ETHER ;;;;;;;;;;
      ;;;;;;;;;;;;;;;;;;;;;;;;;;;

      ;; TODOS
      ;; no straight shapes
      ;; like idea of rotation - make it infinite?
      ;; spirals made of circles
      ;; rough background not ideal use gradient
      ;; good blur
      ;; come up with transition
      ;; make circles blurry

      ;(style {:filter (url (:id disappearing))}
      ;@pixie-dust-anim)))

      ;@shape-shift-anim)))
      ;@splash-g
      ;@splash-y
      ;@splash-yl
      ;@splash-ylo
      ;@splash-plm
      ;@splash-plm)))


(when DEBUG
  (defonce collection (r/atom (cx 1))))

;; ----------- LOOP TIMERS ------------------------------

(defonce frame (r/atom 0))

(when-not DEBUG
  (defonce start-cx-timer
    (js/setInterval
      #(reset! collection (cx @frame)) (* 100 @ms)))

  ;(defonce start-cx-timer-2
  ;  (js/setInterval
  ;    #(reset! collection (cx2 @frame)) 37.5))

  (defonce start-frame-timer
    (js/setInterval
      #(swap! frame inc) (* 1000 @ms))))


;; ----------- DEFS AND DRAW ------------------------------

(def gradients
  [[:linearGradient {:key (random-uuid) :id "grad1"}
    [:stop {:offset "0" :stop-color "yellow" :stop-opacity "0.8"}]
    [:stop {:offset "1" :stop-color "green" :stop-opacity "0.8"}]]

   [:radialGradient {:key (random-uuid) :id "grad2" :cx 0.15 :cy 0.15 :r 1}
    [:stop {:offset "0" :stop-color "purple" :stop-opacity "0.8"}]
    [:stop {:offset "1" :stop-color "silver" :stop-opacity "0.8"}]]

   [:radialGradient {:key (random-uuid) :id "grad3" :cx 0.5 :cy 0.5 :r 0.6 :fx 0.75 :fy 0.75}
    [:stop {:offset "0" :stop-color "purple" :stop-opacity "0.8"}]
    [:stop {:offset "1" :stop-color "silver" :stop-opacity "0.8"}]]])

(def mask-list
  [["poly-mask" [:path {:d b2
                        :fill "#fff"
                        :style {:transform-origin "center"
                                :animation "woosh 2s infinite"}}]]

   ["poly-mask-2" [:path {:d b3
                          :fill "#fff"
                          :style {:transform-origin "center"
                                  :animation "woosh-3 3s infinite"}}]]

   ["grad-mask" [:circle {:cx (* 0.5 @width)
                          :cy (* 0.5 @height)
                          :r 260
                          :fill "url(#grad)"}]]

   ["cutout" (->>
               (gen-rect white
                         10
                         12
                         (* 0.94 @width)
                         (* 0.88 @height))
               (draw))

             (->>
               (gen-circ "#000"
                         (* 0.7 @width)
                         (* 0.7 @height)
                         100)
               (draw))]

   ["rect-buds" (->>
                  (gen-rect white 10 12 (* 0.3 @width) (* 0.5 @height))
                  (draw))]])

   ;["na" [:image {:key (random-uuid)
   ;               :x "0"
   ;               :y "0"
   ;               :width "100%"
   ;               :height "100%"
   ;               :xlinkHref "img/blop.png"
   ;               :style {:transform-origin "center"
   ;                       :transform "scale(2)"}}]]
   ;["nn" [:image {:key (random-uuid)
   ;               :x "100"
   ;               :y "200"
   ;               :width "100%"
   ;               :height "100%"
   ;               :xlinkHref "img/blop.png"
   ;               :style {:transform-origin "center"
   ;                       :transform "scale(10)"
   ;                       :animation "woosh 6s infinite"}}]]])



(def masks
  (map (fn [[id & rest]] (apply gen-mask id rest)) mask-list))

;; TODO update
(def all-filters [turb noiz soft-noiz disappearing splotchy blur])
(def all-fills [gray mint navy blue orange pink white yellow midnight])

(def mix-blend-modes
  ["luminosity" ;; 0
   "difference" ;; 1
   "multiply"   ;; 2
   "normal"     ;; 3
   "screen"     ;; 4
   "overlay"    ;; 5
   "darken"     ;; 6
   "lighten"    ;; 7
   "color-dodge";; 8
   "color-burn" ;; 9
   "hard-light" ;; 10
   "soft-light" ;; 11
   "exclusion"  ;; 12
   "hue"        ;; 13
   "saturation" ;; 14
   "color"])    ;; 15

(defn set-mode [index freq]
  (->> (mix-blend-modes index)
       (repeat freq)))

(defn set-all-modes [modes]
  (-> (map (fn [[index freq]]
             (set-mode index freq))
           modes)
      flatten))

(defn drawing []
  [:svg {:style {:mix-blend-mode (val-cyc @frame (set-all-modes [[3 3]]))}
         :width  (:width settings)
         :height (:height settings)}

    ;;;;;;;;;;;;;
    ;; filters ;;
    ;;;;;;;;;;;;;
    (map #(:def %) all-filters)

    ;;;;;;;;;;;;;;;;;;;;;;;;
    ;; masks and patterns ;;
    ;;;;;;;;;;;;;;;;;;;;;;;;
    [:defs
     noise
     [:circle {:id "weeCirc"
               :cx 0 :cy 0 :r 4
               :style {:animation "colorcolor 100s infinite"
                       :opacity 0.6}}]
     [:circle {:id "testCirc"
               :cx 0 :cy 0 :r 100
               :fill (pattern (str "noise-" white))}]
     [:circle {:id "testCirc2"
               :cx 0 :cy 0 :r 100
               :fill (pattern (str "noise-" mint))}]
     [:circle {:id "testCirc3"
               :cx 0 :cy 0 :r 100
               :style {:animation "colorcolorcolor 10s infinite"
                       :fill (pattern (str "noise-" yellow))}}]

     [:circle {:id "testCirc3"
               :cx 0 :cy 0 :r 100
               :style {:animation "colorcolorcolor 10s infinite"
                       :fill (pattern (str "noise-" yellow))}}]

     [:path {:id "mypent"
             :d pent
             :fill "#DFDC16"
             :style {:transform "scale(0.1)"}}]

     (map identity gradients)
     (map identity masks)
     (map gen-color-noise all-fills)
     (map pattern-def [blue-dots
                       blue-lines
                       pink-dots pink-dots-1 pink-dots-2 pink-dots-3 pink-dots-4 pink-dots-5
                       pink-lines
                       gray-dots
                       gray-dots-lg
                       gray-lines
                       gray-patch
                       mint-dots
                       mint-lines
                       navy-dots
                       navy-lines
                       orange-dots
                       orange-lines
                       br-orange-dots
                       br-orange-lines
                       yellow-dots
                       yellow-lines
                       white-dots
                       white-dots-lg
                       white-lines
                       shadow])]

    ;; then here dereference a state full of polys
    @collection])

(r/render-component [drawing]
  (js/document.getElementById "app-container"))

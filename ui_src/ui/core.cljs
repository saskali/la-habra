(ns ui.core
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.string  :refer [split-lines split join]]
            [ui.helpers :refer [cos sin style url val-cyc]]
            [ui.shapes :refer [tri square pent hex hept oct
                               b1 b2 b3 b4]]
            [ui.fills :refer
              [gray mint navy blue midnight orange pink white yellow
               blue-set-2]]
            [ui.generators :refer
             [freak-out new-freakout scatter lerp
              gen-circ gen-line gen-poly gen-rect gen-shape draw
              gen-group gen-offset-lines gen-bg-lines gen-mask
              gen-grid gen-line-grid gen-cols gen-rows]]
            [ui.filters :refer [turb noiz soft-noiz disappearing splotchy blur]]
            [ui.patterns :refer
             [ gen-color-noise pattern pattern-def
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
            [ui.animations :refer
              [ make-body splice-bodies make-frames!
                nth-frame even-frame odd-frame
                seconds-to-frames frames-to-seconds
                anim anim-and-hold]]))


(enable-console-print!)

(println "Loaded.")

;; hides heads up display for performance
(defn hide-display []
  (let [heads-up-display (.getElementById js/document "figwheel-heads-up-container")]
    (.setAttribute heads-up-display "style" "display: none")))


;; ------------------------ SETTINGS  ---------------------

(def width (atom 1920)) ;;(atom (.-innerWidth js/window)))
(def height (atom 1080)) ;;(atom (.-innerHeight js/window)))

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

; "fade-in-out" "fade-out" "wee-oo" "rot" "rev"

;; --------------- ANIMATIONS STORAGE --------------------

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

(fade-start! "fi" 1)

(make-frames! "etof" [0 100] (make-body "transform" ["translateY(10px)" "translateY(1000px)"]))

(back-and-forth! "scaley" "scale(1)" "scale(15)")
(back-and-forth! "scaley-huge" "scale(20)" "scale(50)")


(a-to-b! "new-fi" "fill-opacity" "0" ".5")
(a-to-b! "bbll" "fill" pink white)
(a-to-b! "sc-rot" "transform" "scale(4) rotate(0deg)" "scale(30) rotate(-80deg)")
(a-to-b! "slide-up" "transform" "translateY(125%)" (str "translateY("(* 0.15 @height)")"))
(a-to-b! "grow2to3" "transform" "rotate(280deg) scale(1)" "rotate(280deg) scale(1.2)")

(fade-start! "fi" 1)

(make-frames!
  "supercolor"
    [10, 35, 55, 85, 92]
   (make-body "fill" [pink pink yellow midnight midnight]))

(make-frames!
  "woosh"
    [10, 35, 55, 85, 92]
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
    [10, 55, 85, 92]
   (make-body "transform" ["translate(80vw, 10vh) rotate(2deg) scale(2.2)"
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
  [20, 40, 60, 80, 100]
  (make-body "transform" ["translate(10vw, 60vh) rotate(2deg) scale(1.2)"
                          "translate(45vw, 45vh) rotate(-200deg) scale(1.8)"
                          "translate(80vw, 80vh) rotate(0deg) scale(4.2)"
                          "translate(60vw, 20vh) rotate(-300deg) scale(2.4)"
                          "translate(20vw, 40vh) rotate(0deg) scale(1.8)"]))

(make-frames!
  "woosh-6"
  [20, 40, 60, 80, 100]
  (make-body "transform" ["translate(10vw, 60vh) rotate(2deg) scale(0.6)"
                          "translate(45vw, 45vh) rotate(-200deg) scale(2.2)"
                          "translate(80vw, 80vh) rotate(0deg) scale(3.8)"
                          "translate(60vw, 20vh) rotate(-300deg) scale(3.2)"
                          "translate(20vw, 40vh) rotate(0deg) scale(2.2)"]))

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



;; --------------- ATOMS STORAGE --------------------

(def drops
  (atom (map
         #(->>
           (gen-rect white (+ 30 (* % 160)) 10 200 36)
           (anim "etof" "1.2s" "infinite" {:delay (str (* 0.5 %) "s")})
           (draw))
         (range 10))))


(def move-me
  (->>
   (gen-shape (pattern (:id white-dots)) hept)
   (style {:transform-origin "center" :transform "scale(1.4)"})
   (anim "woosh-5" "4s" "infinite")
   (draw)
   (atom)))

(def move-me2
  (->>
    (gen-shape (pattern pink) hept)
    (style {:mix-blend-mode "difference"})
    (anim "woosh-5" "4s" "infinite")
    (draw)
    (atom)))

(def move-me3
  (->>
    (gen-shape mint hept)
    (style {:mix-blend-mode "difference" :transform-origin "center" :transform "scale(1.8)"})
    (anim "woosh-6" "4s" "infinite")
    (draw)
    (atom)))

(def move-me-mirrored
  (->>
    (gen-shape (pattern (:id white-dots)) hept)
    (style {:transform-origin "center" :transform "scale(1.4)"})
    (anim "woosh-7" "4s" "infinite")
    (draw)
    (atom)))

(def move-me2-mirrored
  (->>
    (gen-shape mint hept)
    (style {:mix-blend-mode "difference"})
    (anim "woosh-8" "4s" "infinite")
    (draw)
    (atom)))

(def move-me3-mirrored
  (->>
    (gen-shape (pattern pink) hept)
    (style {:mix-blend-mode "difference" :transform-origin "center" :transform "scale(1.8)"})
    (anim "woosh-8" "4s" "infinite")
    (draw)
    (atom)))

(def bg
  (->>
   (gen-circ (pattern (str "noise-" navy)) (* 0.5 @width) (* 0.5 @height) 1800)
   (style {:opacity 1 :transform-origin "center" :transform "scale(4)"})
   (anim "sc-rot" "3s" "infinite" {:timing "linear"})
   (draw)
   (atom)))


(def bb2
  (->>
    (gen-shape mint oct)
    (style {:transform "translate(10vw, 30vh) scale(2) rotate(45deg)"})
    ;(style {:mix-blend-mode "luminosity" :filter (url (:id noiz))} )
    (style {:mix-blend-mode "luminosity"})
    (anim "woosh" "4s" "infinite")
    (draw)
    (atom)))

(def bb4
  (->>
    (gen-shape yellow oct)
    (style {:transform "translate(10vw, 30vh) scale(2) rotate(45deg)"})
    ;(style {:mix-blend-mode "color-dodge" :filter (url (:id noiz))} )
    (style {:mix-blend-mode "color-dodge"})

    (anim "woosh" "2s" "infinite")
    (draw)
    (atom)))


(def bb3
  (->>
    (gen-shape orange hept)
    (style {:transform "translate(30vw, 44vh) scale(2.4)"})
    (style {:mix-blend-mode "color-burn"})
    (anim "woosh-3" "3s" "infinite")
    (draw)
    (atom)))

(def bb6s
  (->>
    (gen-shape (pattern (:id pink-dots)) oct)
    (style {:transform "translate(10vw, 30vh) scale(2.4)"})
    (style {:mix-blend-mode "color-burn"})
    (anim "woosh-3" "3s" "infinite")
    (draw)
    (atom)))

(def scale-me
  (->>
    (gen-rect (pattern (str "noise-" yellow)) 0 0 @width @height)
    (style {:transform "scale(50)"})
    (anim "scaley-huge" "5s" "infinite")
    (draw)
    (atom)))

(def new-scale
  (->>
   (gen-circ white 0 0 100)
   (style {:opacity 0.7})
   (style {:transform "translate(10vh, 60vh)"})
   (gen-group {:style {:animation "scaley 10s infinite"}})
   (atom)))

(def sc-circ
  (->>
    (gen-circ (pattern (:id orange-lines))
              (* 0.5 @width)
              (* 0.4 @height)
              100)
    (anim "scaley" "5s" "infinite")
    (draw)
    (atom)))

(def mf
  (->>
    (gen-shape white tri)
    (anim "morph" "6s" "infinite")
    (style {:opacity 0.7
            :mix-blend-mode "difference"})
    (draw)
    (gen-group {:style {:transform-origin "center"
                        :transform "translate(-2vw, 10vh) scale(4)"}})))

(defn moving-triangle [frame color]
  (->>
    (gen-shape color tri)
    (style {:transform-origin "center"
            :transform (str "translate(40vw, 40vh) rotate("
                            (val-cyc frame [85 85 85 85 245 245 245 245 -80 -80 -80 -80])
                            "deg) scale(4)")})
    (style {:mix-blend-mode "overlay"})
    (draw)
    (when (nth-frame 2 frame))
    (atom)))

(def POLYGON
  (->>
    (gen-poly (:aquamarine blue-set-2) [100 100 400 400 300 100 200 50])
    (style {:opacity 0.7})
    (anim "woosh" "10s" "infinite")
    (draw)
    (atom)))


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
          (atom)))
    (take 10 (repeatedly #(nth [mint navy navy mint] (rand-int 6))))))



(def bb
  (->>
   (->>
    (gen-grid
      40 28
      {:col 40 :row 40}
      (->>
       (gen-shape mint oct)))
    (map #(style {:mix-blend-mode "difference"}  %))
    (map #(anim "supercolor" (str (rand-int 100) "s") "infinite" %))
    (map draw)
    (map #(gen-group {:style {:transform-origin "center"
                              :transform (str
                                          "rotate(" (rand-int 360) "deg)"
                                          "scale(60) translate(-20vh, -20vh)")}} %)))
   (atom)))

(defonce b
  (scatter 10 (->>
               (gen-circ navy 10 10 60)
               (draw))))

(defonce c
  (scatter 10 (->>
               (gen-circ navy 10 10 60)
               (draw))))

(defonce d
  (scatter 10 (->>
               (gen-circ yellow 10 10 60)
               (style {:mix-blend-mode "screen"})
               (draw))))

(defonce e
  (scatter 10 (->>
               (gen-circ yellow 10 10 60)
               (style {:mix-blend-mode "overlay"})
               (draw))))

(defonce f
  (scatter 10 (->>
               (gen-circ pink 10 10 60)
               (style {:mix-blend-mode "multiply"})
               (draw))))

(defonce g
  (scatter 20 (->>
               (gen-circ pink 10 10 60)
               (style {:mix-blend-mode "multiply"})
               (draw))))

 ;; ----------- COLLECTION SETUP AND CHANGE ----------------

(def DEBUG false)

(when-not DEBUG
  (defonce collection (atom (list))))

;(reset! ran {})

(def rr
  (atom (->> (gen-grid 20
                       30
                       {:col 100 :row 150}
                       (->> (gen-shape mint tri)))
             ;(map #(style styles %))
             ; map #(anim "rot" "10s" "infinite" %))
             (map draw)
             (map #(gen-group {:style {:transform-origin "center"
                                       :transform "scale(2)"}} %))
             (map #(gen-group {:mask (url "bitey")
                               :style {:transform-origin "center"
                                       :animation "rot 10s infinite"}} %)))))

(def rr2
  (atom (->> (gen-grid 20 30
                       {:col 100 :row 150}
                       (->> (gen-shape yellow tri)))
             (map  #(style {:opacity 1 :mix-blend-mode "difference"} %))
             (map #(anim "morph" "5s" "infinite" %))
             (map draw)
             (map #(gen-group {:style {:transform-origin "center"
                                       :transform "scale(2)"}} %))
             (map #(gen-group {:style {:transform-origin "center"
                                       :transform "translate(-200px, -100px)"}} %))
             (map #(gen-group {:style {:transform-origin "center"
                                       :animation "rot 5s infinite"}})))))

(def l1 (lerp))

(defn cx2 [frame])

(defn cx [frame]
  (let
    [colors (->> (mapv #(repeat 10 %) [midnight orange pink yellow])
                 (apply concat)
                 vec)
     {:keys [dark-green aquamarine brown-red orange-red]} blue-set-2
     s (->> (quot frame 4) (.sin js/Math) (.abs js/Math))]

    (list
      ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
      ;;;;;;;;;;;;;;;;;; BACKGROUNDS ;;;;;;;;;;;;;;;;;;;;;;;;
      ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
      (->>
        (gen-rect (val-cyc frame colors) 0 0 "100vw" "100%")
        (style {:opacity 0.9})
        (draw))

      ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
      ;;;;;;;;;;;;;;;;;; PATTERNS ;;;;;;;;;;;;;;;;;;;;;;;;;;;
      ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

      ;;;;;;;;;;;;;;;
      ;;;; FRAME ;;;;
      ;;;;;;;;;;;;;;;
      @move-me3
      @move-me2
      @move-me

      @move-me3-mirrored
      @move-me2-mirrored
      @move-me-mirrored)))


(when DEBUG
  (defonce collection (atom (cx 1))))

;; ----------- LOOP TIMERS ------------------------------

(defonce frame (atom 0))

(when-not DEBUG
  (defonce start-cx-timer
    (js/setInterval
      #(reset! collection (cx @frame)) 37.5))

  ;(defonce start-cx-timer-2
  ;  (js/setInterval
  ;    #(reset! collection (cx2 @frame)) 37.5))

  (defonce start-frame-timer
    (js/setInterval
      #(swap! frame inc) 375)))


;; ----------- DEFS AND DRAW ------------------------------

(def gradients
  [[:linearGradient {:id "grad" :key (random-uuid)}]
   [:stop { :offset "0" :stop-color "white" :stop-opacity "0"}]
   [:stop { :offset "1" :stop-color "white" :stop-opacity "1"}]])

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
                  (draw))]

   ["na" [:image {:key (random-uuid)
                  :x "0"
                  :y "0"
                  :width "100%"
                  :height "100%"
                  :xlinkHref "img/blop.png"
                  :style {:transform-origin "center"
                          :transform "scale(2)"}}]]
   ["nn" [:image {:key (random-uuid)
                  :x "100"
                  :y "200"
                  :width "100%"
                  :height "100%"
                  :xlinkHref "img/blop.png"
                  :style {:transform-origin "center"
                          :transform "scale(10)"
                          :animation "woosh 6s infinite"}}]]])



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
  [:svg {:style {:mix-blend-mode (val-cyc @frame (set-all-modes [[2 3] [1 3]]))}
         :width  (:width settings)
         :height (:height settings)}
     ;; filters
    (map #(:def %) all-filters)
    ;; masks and patterns
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
               :style {:animation "colorcolorcolor 100s infinite"
                       :fill (pattern (str "noise-" yellow))}}]
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


(reagent/render-component [drawing]
                          (js/document.getElementById "app-container"))

;(hide-display)

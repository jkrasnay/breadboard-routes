#!/usr/bin/env lumo
(ns routes.routes
  (:require
    [cljs.reader :as reader]
    [clojure.pprint :as pprint]
    [clojure.test :refer [deftest is run-tests]]
    fs))

; Breadboard Routing Utility
;
; Each chip on the breadboard is identified with a keyword, conventionally with the letter "u"
; and a number, e.g. :u2
;
; A 'pin' is specified as either a vector with a chip ID and a pin number, like this: [:u1 3],
; or it identifies one of the power rails :+ or :-
;
; A 'pos' is a position on the breadboard. It's either a vector like this: [:top 36] or this: [:bottom 27],
; or one of the power rails :+ or :-
;

(defn pin-to-pos
  "Returns a vector of [side row], where side is :top or :bottom,
  corresponding to the given chip and pin on the given layout."
  [layout pin]
  (if (keyword? pin)
    pin
    (let [[chip pin-num] pin
          {:keys [pins row]} (get-in layout [:chips chip])]
      (if (> pin-num (/ pins 2))
        [:top (+ row (- pins pin-num))]
        [:bottom (+ row (dec pin-num))]))))

(deftest pin-to-pos-test
  (let [layout {:chips {:u1 {:pins 20 :row 3}}}]
    (is (= [:bottom 3] (pin-to-pos layout [:u1 1])))
    (is (= [:bottom 12] (pin-to-pos layout [:u1 10])))
    (is (= [:top 12] (pin-to-pos layout [:u1 11])))
    (is (= [:top 3] (pin-to-pos layout [:u1 20])))
    (is (= :+ (pin-to-pos layout :+)))))

(def trim-len
  "Distance we add to the length to account for the trimmed jumper tails"
  5)

(defn length
  "Returns the length of the wire in tenths of an inch, given from and to positions.
  This includes overage for trimming and for switching from top to bottom."
  [from to]
  (let [discriminator (apply hash-set (map #(if (keyword? %1) %1 (first %1)) [from to]))]
    (condp = discriminator
      #{:top :+} (+ 3 trim-len)
      #{:top :-} (+ 4 trim-len)
      #{:bottom :+} (+ 4 trim-len)
      #{:bottom :-} (+ 3 trim-len)
      (let [[from-side from-row] from
            [to-side to-row] to]
        (+ (Math.abs (- to-row from-row))
           (if (= from-side to-side) 0 5)
           trim-len)))))

(deftest length-test
  (is (= 9 (length [:bottom 1] [:bottom 5])))
  (is (= 14 (length [:bottom 1] [:top 5])))
  (is (= 8 (length [:top 1] :+)))
  (is (= 8 (length :+ [:top 1])))
  (is (= 9 (length [:bottom 1] :+)))
  (is (= 9 (length :+ [:bottom 1])))
  (is (= 9 (length [:top 1] :-)))
  (is (= 9 (length :- [:top 1])))
  (is (= 8 (length [:bottom 1] :-)))
  (is (= 8 (length :- [:bottom 1]))))

(defn route-1
  "Routes a single wire"
  [layout link]
  (let [[from-pin to-pin color desc] link
        from (pin-to-pos layout from-pin)
        to   (pin-to-pos layout to-pin)]
    {:from from
     :to to
     :length (length from to)
     :color color
     :desc desc}))

; Hack cuz *command-line-args* is broken in Lumo 1.7"
(def args (drop 3 (js->clj (.-argv js/process))))

(if (empty? args)
  (println "Usage: ./routes.cljs filename.edn")
  (if (= ":test" (first args))
    (run-tests 'routes.routes)
    (let [layout (-> (first args) fs/readFileSync str reader/read-string)]
      (pprint/print-table [:from :to :length :color :desc] (map (partial route-1 layout) (:links layout))))))


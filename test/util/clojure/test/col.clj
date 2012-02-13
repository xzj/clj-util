(ns util.clojure.test.col
  (:use [util.clojure.col])
  (:use midje.sweet))

(facts ""
   (nth-divide nil 2) => ['() nil '()]
   ;(nth-divide [] 2) => ['() '()]
   ;(nth-divide '() 2) => ['() '()]
   ;(nth-divide '(1) 2) => ['(1) '()]
   (nth-divide '(1 2 3) 2) => ['(1 2) 3 '()]
   (nth-divide '(1 2 3 4) 2) => ['(1 2) 3 '(4)]
   (nth-divide [1 2 3 4] 2) => ['(1 2) 3 '(4)]
   )

(facts ""
   (nth-divide-in nil []) => []
   (nth-divide-in nil [2]) => [['() nil '()]]
   (nth-divide-in [1 [2 [3 4] 5] 6] [1 1]) => [['(1) [2 [3 4] 5] '(6)] ['(2) [3 4] '(5)]]
   )

(facts ""
   (get-in-seq nil nil) => nil
   (get-in-seq [] nil) => nil
   (get-in-seq [] []) => nil
   (get-in-seq nil [3]) => nil
   (get-in-seq '() '(3)) => nil
   (get-in-seq '(a b [1 2 (3 4) 5 6] cd) '(2 2 1)) => 4 
   )

(facts ""
   (update-in-seq '(a b c) [1] (fn [& args] (identity 1))) => '(a 1 c)
   )


(ns util.clojure.test.core
  (:use [util.clojure.core])
  ;(:use [clojure.test])
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

(facts "test extract-let"
   (extract-let :let '[a 1 b 2] '[c 3 d 4]) => '[(clojure.core/let [a 1 b 2]) ([c 3 d 4])]
   (extract-let :let '[a 1 b 2]) => '[(clojure.core/let [a 1 b 2]) nil]
   )
(facts "test extract-loop"
   (extract-loop '[a 1 b 2]) => '[(clojure.core/loop [a 1 b 2]) []]
   (extract-loop '[a 1 b 2 :let [c 3]]) => '[(clojure.core/loop [a 1 b 2]) (:let [c 3])]
   )
(facts "test extract"
   (extract [:let '[a 1 b 2]]) => '[(clojure.core/let [a 1 b 2]) nil]
   (extract [:let '[a 1 b 2] '[c 3 d 4]]) => '[(clojure.core/let [a 1 b 2]) ([c 3 d 4])]
   (extract '[a 1 b 2 :let [c 3]]) => '[(clojure.core/loop [a 1 b 2]) (:let [c 3])]
   )
(facts "test create-bindings"
   (create-bindings nil) => []
   (create-bindings '[:let [a 1 b 2]]) => '[(clojure.core/let [a 1 b 2])]
   (create-bindings '[:let [a 1 b 2] c 3 d 4]) => '[(clojure.core/let [a 1 b 2]) (clojure.core/loop [c 3 d 4])]
   (create-bindings '[:let [a 1 b 2] c 3 d 4 :let [e 5 d 6]]) => '[(clojure.core/let [a 1 b 2]) (clojure.core/loop [c 3 d 4]) (clojure.core/let [e 5 d 6])]
   (create-bindings '[c 3 d 4 :let [e 5 d 6]]) => '[(clojure.core/loop [c 3 d 4]) (clojure.core/let [e 5 d 6])]
   )

(facts "test loop-with-let expansion"
   (loop-with-let [:let [a 1]] (inc a)) =expands-to=> (clojure.core/let [a 1] (inc a))
   (loop-with-let [:let [a 1] b a]) =expands-to=> (clojure.core/let [a 1] (clojure.core/loop [b a]))
   (loop-with-let [:let [a 1] b a :let [c b]]) =expands-to=> (clojure.core/let [a 1] (clojure.core/loop [b a] (clojure.core/let [c b])))
   (loop-with-let [b a :let [c b]]) =expands-to=> (clojure.core/loop [b a] (clojure.core/let [c b]))
   )
(facts "test loop-with-let behavior"
   (loop-with-let [:let [a 1]] (inc a)) => 2
   (loop-with-let [:let [a 1] b a cnt 2] (if (= b 3) cnt (recur (inc b) (* cnt cnt)))) => 16 
   (loop-with-let [:let [a 1] b a :let [cnt 2]] (if (= b 3) (* cnt b) (recur (inc b)))) => 6 
   (loop-with-let [b 1 :let [cnt 2]] (if (= b 3) (* cnt b) (recur (inc b)))) => 6 
   )

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

(def extract-let @#'util.clojure.core/extract-let)
(facts ""
   (extract-let [] [] []) => [[] [] []]
   (extract-let [] [] '[a b]) => [[] [] '[a b]]
   (extract-let [] [] '[:let [a b]]) => (just [`(let ~'[a b]) [] empty?])
   (extract-let [] [] '[:let [a b] c d]) => [`(let ~'[a b]) [] '(c d)]
   (extract-let `(let ~'[c d]) [] '[:let [a b]]) => (just [`(let ~'[c d] (let ~'[a b])) [2] empty?])
   (extract-let `(let ~'[c d] (loop ~'[e f])) [2] '[:let [a b]]) => (just [`(let ~'[c d] (loop ~'[e f] (let ~'[a b]))) [2 2] empty?])
   )

(def extract-loop @#'util.clojure.core/extract-loop)
(facts ""
   ;read TODO(extract-loop [] [] [:let]) => [[] [] [:let]]
   ;read TODO(extract-loop [] [] []) => [[] [] []]
   (extract-loop [] [] '[a b]) => [`(loop ~'[a b]) [] '()]
   (extract-loop `(let ~'[a b]) [] '[c d]) => [`(let ~'[a b] (loop ~'[c d])) [2] '()]
   (extract-loop `(let ~'[a b]) [] '[c d :let]) => [`(let ~'[a b] (loop ~'[c d])) [2] [:let]]
   )

(def create-bindings @#'util.clojure.core/create-bindings)
(facts ""
   (create-bindings nil) => [nil []]
   (create-bindings []) => [nil []]
   (create-bindings '(:let [a b])) => [`(let ~'[a b]) []]
   (create-bindings '(:let [a b] :let [c d])) => [`(let ~'[a b] (let ~'[c d])) [2]]
   (create-bindings '(:let [a b] c d)) => [`(let ~'[a b] (loop ~'[c d])) [2]]
   (create-bindings '(:let [a b] c d e f)) => [`(let ~'[a b] (loop ~'[c d e f])) [2]]
   (create-bindings '(:let [a 0] c 1 e 2 :let [g 3 i 4])) => [`(let ~'[a 0] (loop ~'[c 1 e 2] (let ~'[g 3 i 4]))) [2 2]]
   )

(facts ""
   (loop-with-let [:let [a 1]] (inc a)) =expands-to=> (clojure.core/let [a 1] (inc a))
   (loop-with-let [:let [a 1] b a]) =expands-to=> (clojure.core/let [a 1] (loop [b a]))
   )

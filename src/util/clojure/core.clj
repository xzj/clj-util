(ns util.clojure.core)

(defn nth-divide [coll n]
  [(take n coll) (nth coll n) (drop (inc n) coll)])
(defn nth-divide-in [coll indices]
  (reduce #(let [last-one (if (empty? %1) coll (second (last %1)))] (conj %1 (nth-divide last-one %2))) [] indices))
(defn get-in-seq [coll indices]
  (if (empty? indices)
    nil
    (reduce #(first (nthnext %1 %2)) coll indices)))
(defn update-in-seq [coll [index & remain :as indices] f & args]
  (let [nth-divisions (nth-divide-in coll indices)
        rev-divisions (rseq nth-divisions)
        result (apply f (second (first rev-divisions)) args)]
    (reduce #(concat (first %2) (list %1) (last %2)) result rev-divisions)))

(defn- extract-let [ret index-vec [key val & others-binding :as all-binding]]
   (if-not (= key :let)
     [ret index-vec all-binding]
     (let [let-cons `(let ~val)]
       (cond
         (and (empty? ret) (empty? index-vec)) [let-cons [] others-binding]
         (and (seq ret) (empty? index-vec)) [(concat ret (list let-cons)) [2] others-binding]
         :else [(seq (update-in (vec ret) index-vec #(concat % (list let-cons)))) (conj index-vec 2) others-binding]))))

(defn seq-before-let-keyword [coll]
  (flatten (for [pair (partition 2 coll) :while (not= (first pair) :let)] pair)))
(defn half-by-let-keyword [coll]
  (let [before-let (seq-before-let-keyword coll)]
    [before-let (drop (count before-let) coll)]))
(defn- extract-loop [ret index-vec all-binding]
    (let [[loop-binding remain] (half-by-let-keyword all-binding)
          loop-cons `(loop ~loop-binding)]
      (cond
        ;TODO (empty? loop-binding) (should throw exception)
        (and (empty? ret) (empty? index-vec)) [loop-cons index-vec remain]
        (and (seq ret) (empty? index-vec)) [(concat ret (list loop-cons)) [2] remain]
        ;TODO :else (should throw exception)
        )))
;)

(defmulti extract #(and (seq %3) (= (first %3) :let)))
(defmethod extract nil [result index-vec bindings]
  [result index-vec bindings])
(defmethod extract true [result index-vec bindings]
  (extract-let result index-vec bindings))
(defmethod extract false [result index-vec bindings]
  (extract-loop result index-vec bindings))

(defmulti extract2 #(= (first %) :let))
(defn extract-let2 [_let-sym let-bindings & other-bindings]
  [`(let ~let-bindings) other-bindings])
(defn extract-loop2 [bindings]
  (let [last-binding (take-last 2 bindings)
        let-end? (= (first last-binding) :let)
        [loop-bindings other-bindings] (if let-end?
                                           [(vec (drop-last 2 bindings)) last-binding]
                                           [(vec bindings) []])]
  [`(loop ~loop-bindings) other-bindings]))
(defn create-bindings2 [bindings]
  (loop [[ret remain-bindings] [[] bindings]]
    (if (empty? remain-bindings)
      ret
      (let [[this-ret other-bindings] (extract2 remain-bindings)]
        (recur [(conj ret this-ret) other-bindings])))))

(defn- create-bindings [bindings]
   (loop [binding-forms nil
          index-vec []
          remain-bindings bindings]
     (if (empty? remain-bindings)
       [binding-forms index-vec]
       (let [[new-binding-forms new-index-vec remain] (extract binding-forms index-vec remain-bindings)]
         (recur new-binding-forms new-index-vec remain)))))
(defmacro loop-with-let [bindings & actions]
  (let [[binding-forms index-vec] (create-bindings bindings)]
    (if (empty? index-vec)
      (concat binding-forms actions)
      (update-in binding-forms index-vec #(concat %1 %2) actions)
      ;(update-in binding-forms index-vec #(concat (get-in binding-forms index-vec) %) actions)
      )))

(defmacro loop-with-let2 [bindings & actions]
  (->> (xx bindings)
       reverse
       (reduce #(concat %2 %1) actions)))

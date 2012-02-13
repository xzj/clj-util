(ns util.clojure.core)

(defn extract-let [_let-sym let-bindings & other-bindings]
  [`(let ~let-bindings) other-bindings])
(defn extract-loop [bindings]
  (let [last-binding (take-last 2 bindings)
        let-end? (= (first last-binding) :let)
        [loop-bindings other-bindings] (if let-end?
                                           [(vec (drop-last 2 bindings)) last-binding]
                                           [(vec bindings) []])]
  [`(loop ~loop-bindings) other-bindings]))

(defmulti extract #(= (first %) :let))
(defmethod extract true [bindings]
  (apply extract-let bindings))
(defmethod extract false [bindings]
  (extract-loop bindings))

(defn create-bindings [bindings]
  (loop [ret [] remain-bindings bindings]
    (if (empty? remain-bindings)
      ret
      (let [[this-ret other-bindings] (extract remain-bindings)]
        (recur (conj ret this-ret) other-bindings)))))

(defmacro loop-with-let [bindings & actions]
  (let [binding-seq (->> (create-bindings bindings)
        reverse)]
    (reduce #(concat %2 (list %1)) (-> (first binding-seq) (concat actions)) (drop 1 binding-seq))))

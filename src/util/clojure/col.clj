(ns util.clojure.col)

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

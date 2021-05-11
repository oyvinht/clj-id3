(ns id3.core)

;; Utilities
;;----------
(defn- subset [examples attribute value]
  "Return a list of the 'examples' that have 'value' for 'attribute'."
  (reduce
   (fn [res item] (if (= (get item attribute) value) (cons item res) res))
   nil examples))

(defn- attr-val-counts [attribute examples]; &key values-only]
  "Returns a map of attribute values and their count."; (or just the values)."
  (reduce (fn [res item] (let [val (get item attribute)]
                           (assoc res val (inc (get res val 0)))))
          {} examples))

(defn- max-map-entry [map-entries]
  (reduce (fn [best current]
            (if (> (val current) (val best)) current best))
          (first map-entries)
          (rest map-entries)))

(defn- log [num base]
  "Natural logarithm of a number in any base."
  (/ (java.lang.Math/log num) (java.lang.Math/log base)))

;; ID3 functions
;;--------------
(defn- entropy [examples target-attribute]
  "Percent hetereogenity of examples with regard to the target-attribute."
  (let [distinct-values (attr-val-counts target-attribute examples)
        total (count examples)]
    (reduce ; Sum together [- [proport. of i] times log_2 [proport. i]]
     (fn [res value]
       (let [prop-i
             (/ (count (subset examples target-attribute (first value))) total)]
         (+ res (- (* prop-i (log prop-i 2))))))
     0 distinct-values)))

(defn- info-gain [attribute examples target-attribute]
  "Calculates the expected reduction in entropy by classifying on 'attribute'."
  (let [total (count examples)]
    (- (entropy examples target-attribute)
       (reduce
        (fn [res value]
          (+ res
             (let [value-subset (subset examples attribute (first value))]
               (* (/ (count value-subset) total)
                  (entropy value-subset target-attribute)))))
        0 (attr-val-counts attribute examples)))))

(defn- most-info-gaining-attr [attributes examples target-attribute]
  "Return the attribute that best separates examples."
  (key (reduce
        (fn [best attr]
          (max-map-entry
           (conj {attr (info-gain attr examples target-attribute)} best)))
        nil attributes)))

(defn- most-common-attr-val [attribute examples]
  "Return the most common value of 'attribute' in 'examples'."
  (key (max-map-entry (attr-val-counts attribute examples))))

(defn id3
  ([examples target-attribute positive-value negative-value]
   (id3 examples target-attribute positive-value negative-value
        (remove #{target-attribute} (set (mapcat keys examples)))))
  ([examples target-attribute positive-value negative-value attributes]
   "Induce tree for deciding the target-attribute (only for 'attrs', if given)."
   (let [num-positive (count (subset examples target-attribute positive-value))]
     (cond
       ;; If all examples are positive, return the root labeled 'positive-value'
       (= num-positive (count examples)) positive-value
       ;; -- no  -- || --                                       'negative-value'
       (= num-positive 0) negative-value
       ;; Attributes empty -> Pick root labaled w/most common 'target-attribute'
       (empty? attributes) (most-common-attr-val target-attribute examples)
       ;; Otherwise create subtree for value with highest info gain
       true
       (let [attr (most-info-gaining-attr attributes examples target-attribute)
             attr-vals (keys (attr-val-counts attr examples))]
         {attr
          (reduce
           (fn [tree attr-val]
             (let [sub (subset examples attr attr-val)]
               (assoc tree
                      attr-val
                      (if (empty? sub)
                        (most-common-attr-val target-attribute examples)
                        (id3 sub
                             target-attribute
                             positive-value
                             negative-value
                             (remove #{attr} attributes))))))
           {}
           attr-vals)})))))

;(defn classify [instance decision-tree]
  

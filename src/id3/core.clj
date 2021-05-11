(ns id3.core)

;; Utilities
;;----------
(defn subset [examples attribute value]
  "Return a list of the 'examples' that have 'value' for 'attribute'."
  (reduce
   (fn [res item] (if (= (get item attribute) value) (cons item res) res))
   nil examples))

(defn attr-val-counts [attribute examples]; &key values-only]
  "Returns a map of attribute values and their count."; (or just the values)."
  (reduce (fn [res item] (let [val (get item attribute)]
                           (assoc res val (inc (get res val 0)))))
          {} examples))

(defn max-map-entry [map-entries]
  (reduce (fn [best current]
            (if (> (val current) (val best)) current best))
          (first map-entries)
          (rest map-entries)))

(defn log [num base]
  "Natural logarithm of a number in any base."
  (/ (java.lang.Math/log num) (java.lang.Math/log base)))

;; ID3 functions
;;--------------
(defn entropy [examples target-attribute]
  "Percent hetereogenity of examples with regard to the target-attribute."
  (let [distinct-values (attr-val-counts target-attribute examples)
        total (count examples)]
    (reduce ; Sum together [- [proport. of i] times log_2 [proport. i]]
     (fn [res value]
       (let [prop-i
             (/ (count (subset examples target-attribute (first value))) total)]
         (+ res (- (* prop-i (log prop-i 2))))))
     0 distinct-values)))

(defn info-gain [attribute examples target-attribute]
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

(defn most-info-gaining-attr [attributes examples target-attribute]
  "Return the attribute that best separates examples."
  (key (reduce
        (fn [best attr]
          (max-map-entry
           (conj {attr (info-gain attr examples target-attribute)} best)))
        nil attributes)))

(defn most-common-attr-val [attribute examples]
  "Return the most common value of 'attribute' in 'examples'."
  (key (max-map-entry (attr-val-counts attribute examples))))


(defn id3
  ([examples target-attribute positive-value negative-value]
   (id3 examples target-attribute positive-value negative-value
        (remove #{target-attribute} (set (mapcat keys examples)))))
  ([examples target-attribute positive-value negative-value attributes]
   "Induce tree for deciding the target-attribute (only for 'attrs', if given)."
   ;;(println "attributes:" attributes)
   (let [num-positive (count (subset examples target-attribute positive-value))]
     (cond
       ;; If all examples are positive, return the root labeled 'positive-value'
       (= num-positive (count examples)) (do positive-value)
       ;; -- no  -- || --                                       'negative-value'
       (= num-positive 0) (do negative-value)
       ;; If attributes are empty, return the root labeled with
       ;; the most common value of 'target-attribute'
       (empty? attributes) (do (most-common-attr-val target-attribute examples))
       ;; Otherwise ...
       true
       (let [;; Pick the value with highest info gain.
             attr (most-info-gaining-attr attributes examples target-attribute)
             ;; Fetch all possible values for it
             attr-vals (keys (attr-val-counts attr examples))]
         ;; Create a node labeled by this attribute ...
         (cons
          attr
          (reduce (fn [tree attr-val]
                    (let [sub (subset examples attr attr-val)]
                      (cons
                      (cons attr-val
                            (if (empty? sub)
                              (most-common-attr-val target-attribute examples)
                              (list (id3 sub target-attribute positive-value negative-value (remove #{attr} attributes)))))
                      tree)))
                  nil
                  attr-vals)))))))

;; TODO: Probably better to return an array map...
;; {:outlook {"rain" {:wind {"weak" "yes" "strong" "no"}}
;;            "overcast" "yes"
;;            "sunny" {:humidity {"high" "no" "normal" "yes"}}}}


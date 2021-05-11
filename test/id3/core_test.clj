(ns id3.core-test
  (:require [clojure.test :refer :all]
            [id3.core :refer :all]
            [criterium.core :as criterium]))

(def examples
  [{:outlook "sunny"    :temp "hot"  :humidity "high"   :wind "weak"   :play-tennis "no"}
   {:outlook "sunny"    :temp "hot"  :humidity "high"   :wind "strong" :play-tennis "no"}
   {:outlook "overcast" :temp "hot"  :humidity "high"   :wind "weak"   :play-tennis "yes"}
   {:outlook "rain"     :temp "mild" :humidity "high"   :wind "weak"   :play-tennis "yes"}
   {:outlook "rain"     :temp "cool" :humidity "normal" :wind "weak"   :play-tennis "yes"}
   {:outlook "rain"     :temp "cool" :humidity "normal" :wind "strong" :play-tennis "no"}
   {:outlook "overcast" :temp "cool" :humidity "normal" :wind "strong" :play-tennis "yes"}
   {:outlook "sunny"    :temp "mild" :humidity "high"   :wind "weak"   :play-tennis "no"}
   {:outlook "sunny"    :temp "cool" :humidity "normal" :wind "weak"   :play-tennis "yes"}
   {:outlook "rain"     :temp "mild" :humidity "normal" :wind "weak"   :play-tennis "yes"}
   {:outlook "sunny"    :temp "mild" :humidity "normal" :wind "strong" :play-tennis "yes"}
   {:outlook "overcast" :temp "mild" :humidity "high"   :wind "strong" :play-tennis "yes"}
   {:outlook "overcast" :temp "hot"  :humidity "normal" :wind "weak"   :play-tennis "yes"}
   {:outlook "rain"     :temp "mild" :humidity "high"   :wind "strong" :play-tennis "no"}])

(deftest test-subset
  (testing "See if subset works as intended."
    (is (= [{:outlook "overcast" :temp "hot"  :humidity "normal" :wind "weak"   :play-tennis "yes"}
            {:outlook "overcast" :temp "mild" :humidity "high"   :wind "strong" :play-tennis "yes"}
            {:outlook "overcast" :temp "cool" :humidity "normal" :wind "strong" :play-tennis "yes"}
            {:outlook "overcast" :temp "hot"  :humidity "high"   :wind "weak"   :play-tennis "yes"}]
           (subset examples :outlook "overcast")))))
  
(deftest test-attr-val-counts
  (testing "Test count number of attributes with a specific value."
    (is (= {"sunny" 5 "overcast" 4 "rain" 5}
           (attr-val-counts :outlook examples)))))

(deftest test-entropy []
  (testing "Testing if entropy function works."
    (is (= (float 0.940286)
           (float (entropy examples :play-tennis))))))

(deftest test-info-gain []
  (testing "Testing if info-gain function works."
    (is (= (float 0.1518355)
           (float (info-gain :humidity examples :play-tennis))))))

(deftest test-most-info-gaining-attr []
  (testing "Testing if most-info-gaining-attribute function works."
    (is (= :outlook
           (most-info-gaining-attr [:humidity :outlook :temp :wind] examples :play-tennis)))))

(deftest test-most-common-attr-val []
  (testing "Testing if most-common-attribute-value function works."
    (is (= "mild"
           (most-common-attr-val :temp examples)))))

(deftest test-id3 []
  (testing "Testing if id3 function works."
    (let [result (id3 examples :play-tennis "yes" "no")]
      (is (= "no" (get-in result [:outlook "rain" :wind "strong"])))
      (is (= "yes" (get-in result [:outlook "overcast"])))
      (is (= "no" (get-in result [:outlook "sunny" :humidity "high"]))))))

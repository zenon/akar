(ns akar.primitives-test
  (:require [clojure.test :refer :all]
            [akar.primitives :refer :all]
            [akar.patterns.basic :refer :all]))

(deftest primitives-test

  (testing "clause"
    (testing "invokes no action if pattern did not match"
      (is (= nil
             ((clause !pfail (fn [] :some-value)) :value))))
    (testing "invokes the action on the captured value if pattern matched"
      (is (= 6
             ((clause !var (fn [x] (* 2 x))) 3)))))

  (testing "clauses"
    (testing "runs through the clauses in order"
      (let [!zero (!pred zero?)
            !odd (!pred odd?)
            block (clauses
                    !zero (fn [] :zero)
                    !odd (fn [] :odd))]
        (testing "runs through the clauses in order"
          (is (= :zero
                 (match 0 block)))
          (is (= :odd
                 (match 9 block)))))))

  (testing "match"
    (testing "throws an error if pattern did not match"
      (is (thrown? RuntimeException
                   (match :some-value (clauses))))))

  (testing "try-match"
    (testing "returns nil if pattern did not match"
      (is (= nil
             (try-match :some-value (clauses))))))

  (testing "or-else"
    (let [[!4 !5 !6] (map !cst [4 5 6])
          !4-or-!5 (or-else !4 !5)
          !4-or-!5-or-!6 (or-else !4 !5 !6)]
      (is (match 6 (clauses
                     !4-or-!5 (fn [] :num)
                     !any (fn [] :any)))
          :any)
      (is (match 6 (clauses
                     !4-or-!5-or-!6 (fn [] :num)
                     !any (fn [] :num)))
          :num))))

;(deftest simple-patterns-test
;
;  (testing "!any"
;    (is (match :random-value (clauses
;                               !any (constantly :success)))
;        :success))
;
;  (testing "!var"
;    (is (match :some-value (clauses
;                             !var identity))
;        :some-value))
;
;  (testing "!pred"
;    (let [!even (!pred even?)
;          !odd (!pred odd?)
;          block (clauses
;                  !even (constantly :even)
;                  !odd (constantly :odd))]
;      (is (match 9 block)
;          :odd)
;      (is (match 8 block)
;          :even)))
;
;  (testing "!constant"
;    (let [block (clauses
;                  (!cst 4) (constantly :fier)
;                  (!cst 5) (constantly :fünf))]
;      (is (match 4 block)
;          :fier)
;      (is (match 5 block)
;          :fünf))))

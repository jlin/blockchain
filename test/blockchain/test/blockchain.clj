(ns blockchain.test.blockchain
  (:require [clojure.test :refer :all]
            [blockchain.blockchain :as bc]
            [clj-time.coerce :as tc]))

(deftest test-mining
  (is (= (bc/new-transaction! {:sender "123456789"
                              :recipient "987654321"
                              :amount 5})
         0))
  (is (= @bc/chain [{:index 0,
                     :timestamp 123456789,
                     :transactions [],
                     :hash "9cb345d0ec9bfc248497286166505ed9f643aca499d86b74088000909226c91f",
                     :previous-hash 0,
                     :proof 100}]))
  (with-redefs-fn {#'bc/calculate-hash
                   (fn [_ _ _ _]
                     "ea99ea620ad57eda103070c874bc782cc77215b7bc1bab21eac94ad9bb74385a")
                   #'tc/to-long
                   (fn [_]
                     1507145890144)}
    #(is (= (bc/mine)
            {:hash "ea99ea620ad57eda103070c874bc782cc77215b7bc1bab21eac94ad9bb74385a"
             :index 1
             :previous-hash "9cb345d0ec9bfc248497286166505ed9f643aca499d86b74088000909226c91f"
             :proof 35293
             :timestamp 1507145890144
             :transactions [{:amount 5
                             :recipient "987654321"
                             :sender "123456789"}
                            {:amount 1
                             :recipient bc/node-identifier
                             :sender "0"}]})))
  (is (= @bc/chain [{:hash "9cb345d0ec9bfc248497286166505ed9f643aca499d86b74088000909226c91f"
                     :index 0
                     :previous-hash 0
                     :proof 100
                     :timestamp 123456789
                     :transactions []}
                    {:hash "ea99ea620ad57eda103070c874bc782cc77215b7bc1bab21eac94ad9bb74385a"
                     :index 1
                     :previous-hash "9cb345d0ec9bfc248497286166505ed9f643aca499d86b74088000909226c91f"
                     :proof 35293
                     :timestamp 1507145890144
                     :transactions [{:amount 5
                                     :recipient "987654321"
                                     :sender "123456789"}
                                    {:amount 1
                                     :recipient bc/node-identifier
                                     :sender "0"}]}]))
  (is (= (bc/new-transaction! {:sender "123456789"
                              :recipient "987654321"
                              :amount 5})
         1)))
(ns inside-out.08-concurrency-part-2)
(set! *print-length* 7)

;;;; Agents
;; Succession functions return immediately (the work can't possibly have been done yet)
;; Queue enforced serialization
;; Uncoordinated, deadlock free

;; (send agt f & args)
;; 1:16
;; suitable for calculating intensive tasks

;; (send-off agt f & args)
;; suitable for io intensive tasks

;; (send-via agt f & args)
;; you know what you are doing and want to control everything yourself

;; cheatsheet at 2:00

;;;; Atom
;; 3:20
(def a (atom 0))
(swap! a inc)

;; optimitic concurrency
(compare-and-set! a 0 42)
(compare-and-set! a 1 7)

;;;; Software Transactional Memory
;; (just one instansiation of the atomic succession model)
;; almost not used ..

;; Refs can change only within a transaction
;; Provides the ACI in ACID
;; Transactions are speculative, will be retried
;; 7:50

;;;; You could build a "Lock based reference type"

;;;; Transactions

(defn transfer
  [from to amount]
  (dosync 
   (alter from - amount)
   (alter to + amount)))

(comment
  (alter from - 1)
  )

;;;; Which is Commutative?
(defn next-id
  "Get the next available id."
  [r]
  (dosync (alter r inc))) ;; <--- order matters, stick with _alter_

(defn increment-counter
  "Increment the counter"
  [r]
  (dosync (commute r inc))) ;; <--- safe for commute, don't care about order

;; commutative operations can be done without failing the whole transaction

;;;; STM Details
;; Uses locks, latches internally to avoid churn
;; Deadlock detection and barging
;; No read tracking 
;;  (because clojure does not have to keep track of what everybody looks at .. everything is immutable)
;; Readers never impede writers
;; Nobody impede Readers

;;;; Pending References
;; Represent work that may not be done yet
;; Will only ever refer to one thing
;; Not identities

;;;; Future - start doing this on another thread
(comment
  (def result
    (future (some-long-task))) ;; <--- body executes on pooled thread
  (deref result 1000 or-else)  ;; <--- waith with timeout and timeout value
  @result
  (deref result)
  )

(comment
  (def project-euler-problem
    (future (implememntation-of-the-problem)))
  (deref result 60000 (println "try another implementation"))
  )

;;;; Delay - don't do this just yet
(comment
  (def result (delay dont-need-yet)) ; <--- cache body
  @result
  (deref result)                    ; <--- wait without timeout '@' is suntax sugar as usual
  ;; the body will be executed when you ask for the result via deref
  )

;;;; Promise - somebody else will do the work
(comment
  (def result (promise)) ; <---- no-arg constructor
  (deliver result 42)
  (deref result 1000 or-else)
    @result
  (deref result)
  )
;;;; Don't forget Java APIs!
;; Semaphore
;; ConcurrentMap
;; BlockingQueue
;; Executors
;; CountDownLatch
;; ConcurrentSkipListMap

;; these do not overlap with anything in clojure, so if we need one of these to solve a problem,
;; we need to _use_ one of these.

;; 20:35 illustration that explains and brings together everything

;;;; the end




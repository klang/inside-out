(ns inside-out.07-concurrency-part-1)
(set! *print-length* 7)

;; concurrency, by sort a coincidence of events of space
;; parallelism, by sort the execution of operations concurrently by separate parts of a computer

;; Our tools

;; threads and places .. critical section .. locking .. 

;; memory, the capacity ... for returning to a previous state when the cause of the transition from that state is removed
;; record, the fact or condition of having been written down as evidence ... an authentic or official report

;; Memory, Records = Places ?

;; Memory is small and expensive (you can only have 16k)
;; Storage is small and expensive (it's on cassettes)
;; Machines are precious, dedicated resources (Machines are more expensive than developers)
;; Applications are control centers 

;; a lot of our asumptions are based on 20 or 30 year old technology

;; today, the most expensive resource, by _far_, is the programmer

;;;; A Different Approach
;; New memories use new places
;; New records use new places
;; New moments use new places (introduces the concept of 'time')

;;;; Values
;; Immutable
;; Maybe lazy
;; Cashable (forever!)
;; Can be arbitrarily large
;; Share structure

;;;; What can be a value
;; 42 is a value
;; {:first-name "Stu" :last-name "Halloway"} is a multipart value
;; MusicBrainz.com is a value .. somewhat big, but still a value
;; wikipedia.com also a value
;; Anything?

;;;; References
;; Refer to values (or other values)
;; Permit atomic, functional succession
;; Model _time_ and _identity_
;; (clojure does _not_ ignore _time_ as a first class citizen)
;; Compatible with a wide variety of update semantics

;; Epochal Time Model (relatively unique to Clojure)
;; (7:35)
;; observers perceive identity, can remember and record
;; observers do not coordinate (this is how the real world works)


;;;; API to the Time Model
(def counter (atom 0)) ; <--- reference constructor
(swap! counter + 10)
;; atomic succession ... + is a pure function .. and the argument

;;;; Bigger Structures
(comment
  (def person (atom (create-person)))
  (swap! person assoc :name "John")
)

;;;; Varying Semantics
(def number-later (promise))
(deliver number-later 42)

;;;; Entire Database
(comment
  (d/create uri)
  (def conn (d/connect uri))
  (transact conn ,,, data) ;<-- ,,, = no fn needed .. but the semantics are pretty much the same
)

;;;; cheat sheet 14:43

;;                   Atomic succession functions
;;                   send                        processor-derived pool
;; agent --->        send-off                    IO-derived pool
;;                   send-via                    user-specified pool
                                                 
;;                   compare-and-set!            conditional
;; atom <--->        reset!                      boring
;;                   swap!                       
                                                 
;; connection        transact       <--->        ACID
;; (datomic)         transact-async  --->        ACID
                                                 
;; ref <--->         alter                       
;;                   commute                     commutative
                                                 
;; var <--->         alter-var-root              aplication config
                                                 
;; var binding <---> binding, set!               dynamic, binding-local

;;  ---> async
;; <---> sync

;;;; Shared Abilities

(comment
  @some-ref
  (deref some-ref)
)
;; validator guards succession
(atom 0 :validator integer?)

;; add/remove named watcher fn
(comment
  (add-watch ref :a/name watcher)
  (remove-watch ref :a/name))

;;;; the end
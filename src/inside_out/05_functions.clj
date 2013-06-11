(ns inside-out.05-functions)
(set! *print-length* 7)



;;;; examples of the repl

;;;; ClojureScript example (starts at 15:00)

;;;; Functions (starts at 21:25)

;; Boids Example
;; Boids https://github.com/relevance/boids

;; Algorithm in Clojure
;; Cross-compiled to ClojureScript
;; Whole-program optimization

;; Google Closure .. JavaScript optimizer (Whole program optimization)
;; recommended, but not neccesary .. use it if you want .. 

;; ClojureScript has been designed to produce code that is GoogleClosure friendly

;;;; Functional Programming

;; Immutable data
;; First class functions
;; Referential transparency
;; No side effects
;; Often (but not always!) statically typed
;;  Haskel and Scala are statically typed
;;  Clojure is dynamically strongly typed

;;;; Why FP?

;; Easier to reason about
;; Easier to test (less things that go through state transition)
;; Easier to compose
;; Essential at scale (opinion)

;;;; Persistent Data Structures

;; Composite values - immutable

;;;; Persistent Data Structures

;; Composite values - immutable
;; 'Change' is a function of value to value
;; Collections maintains performance guarantees
;; New versions are not full copies
;; Old version of collection available after 'change'

;;;; Bit-partitioned hash tries

;; (illustration at 31:56)

;;;; Path Copying

;; (illustration at 21:13)

;;;; Constructors

;; Looping via recursion (recur)
;;  prefer higher-order library fns when appropriate
;; Iteration via 'map' and list comprehensions ('for')
;; Accumulation and value building via 'reduce' and 'into'

;;;; Recursive Loops
;; No mutable locals in Clojure
;; No tail recursion optimization in the JVM
;; 'recur' op does constant-space recursive looping
;; Rebinds and loops to nearest 'loop' or function frame

(defn zipm [keys vals]
  (loop [m {}
         ks (seq keys)
         vs (seq vals)]
    (if (and ks vs) 
      (recur (assoc m (first ks) (first vs))
             (next ks)
             (next vs))
      m)))

(zipm [:a :b :c] [1 2 3])

;;;; Loop Alternatives

;; reduce with adder fn
(defn zipm [keys vals]
  (reduce (fn [m [k v]] (assoc m k v)) {} (map vector keys vals)))

;; apply data constructor fn
(defn zipm [keys vals]
  (apply hash-map (interleave keys vals)))

;; map into empty (or not!) structure
(defn zipm [keys vals]
  (into {} (map vector keys vals)))

;; get lucky
(zipmap [:a :b :c] [1 2 3]) ; already in there

;;;; Data Abstractions
;; Mamy general functions, few data structures
;; Conform with platform interfaces
;; Provide more granular interfaces
;; Seqs (traverse something without changing it) - the mutable cosin: Iterator
;; Laziness
;; Callability

;;;; Benefits of Abstractions

"It's better to have 100 functions operate on 
one data structure than to have 10 functions
operate on 10 data structures" ;- Alan J. Perlis

"Better still .. 100 functions per abstraction"

;; E.g. 'seq', implemented for all Clojure Collections, all Java collections
;; Strings, regex matches, files etc.

;; Many library functions defined on seqs

;;;; Seq - Sequences
;; Abstraction of traditional Lisp lists

;; (seq coll)
;;  if collection is non-empty, return seq object on it, else nil

;; (first seq)
;;  returns the first element

;; (rest seq)
;;  returns a sequence of the rest of the elements

;;;; Lazy Seqs (50:41)

;; Not proudced until (and as) requested
;; Define your own lazy seq-producing functions using the 'lazy-seq' macro
;; Seqs can be used like generators
;; Lazy and concrete seqs interoperate - no separate lazy library

;; the library function take
(defn take [n coll]
  (lazy-seq
   (when-let [s (seq coll)]
     (cons (first s) (take (dec n) (rest s))))))

;; the call to 'take' is not recursive! it's defered, it's lazy

;;;; Laziness

;; Most of the core library function sthat produce sequences do so lazily
;;  e.g. map, filter etc.
;;  And thus if they consume sequences, do so lazily as well
;; Avoids creating full intermediate results
;; Create only as much as much as you consume
;; Work with infinite sequences, datasets larger than memory

;;;; Sequences

(drop 2 [1 2 3 4 5])
(take 9 (cycle [1 2 3 4]))
(interleave [:a :b :c :d :e] [1 2 3 4 5])
(partition 3 [1 2 3 4 5 6 7 8 9 ])
(map vector [:a :b :c :d :e] [1 2 3 4 5])
(apply str (interpose \, "asdf"))
(reduce + (range 100))

;;;; Sequence omprehensions (for) (58:04)

;; Lazy sequence generator/consumer
;;  for is not an imperative loop! (it's just a name clash)
;; control and binding clauses:
;;  :when, :while, :let
;; 'for' is a mini-language inside clojure (a dsl)

(for [x (range 2) y (range 3)] [x y])
(take 20 (for [x (range 1000000) y (range 100000) :while (< y x)] [x y]))

;;;; Seq Cheat Sheet
;; clojure.org/sheatsheet

;;;; Vectors
(def v [42 :rabbit [1 2 3]])
(get v 1)
(v 1)
(peek v)
(pop v)
(subvec v 1)
(contains? v 0)
(contains? v 42)

;;;; Maps
(def m {:a 1 :b 2 :c 3})

(m :b)
(:b m)
(keys m)
(assoc m :b 4 :c 42)
(dissoc m :d)
(merge-with + m {:a 2 :b 3})

;;;; Nested Structures
(def jdoe {:name "John Doe"
           :address {:zip 27705 }})

(get-in jdoe [:address :zip]) ;; the vector is a path to find the data
(assoc-in jdoe [:address :zip] 27514)
(update-in jdoe [:address :zip] inc)

;; error at 1:10:12 (use clojure.set) should be (use 'clojure.set)
;;;; Sets
(use 'clojure.set)
(def colors #{"red" "green" "blue"})
(def moods #{"happy" "blue"})

(disj colors "red")
(difference colors moods)
(difference moods colors)
(intersection colors moods)
(union colors moods)

;; bonus: all relational algebra primitives supported for sets-of-maps

;;;; Callability (Part 1)
;; graph at 1:12:43

;;;; Callability
;; (can-go-here ...)
;; (map or-here ...), (filter or-here ...) etc
;; Calling a collection is a lookup
;; Symbols, keywords invocation is a self-lookup
;; Vars and refs delegate to their values

;;;; Other Attributes
;; Counted
;;  count is O(1)
;; Sorted
;;  subseq, rsubseq
;; Associative
;;  assoc, map-style destructuring
;; Reversible
;;  rseq

;;;; Addition
;;;; Find the Consistency
(def v [1 2 3])
(def l '(1 2 3))

(conj v 42) ;--> [1 2 3 42]
(conj l 42) ;--> (42 1 2 3)

(into v [99 :bottles]) ;--> [1 2 3 99 :bottles]
(into l [99 :bottles]) ;--> '(:bottles 99 1 2 3) OBS! [99 :bottles] is reversed!

;;;; Addition

;; Collection        Addition point
;; seq               front
;; list              front
;; vector            back
;; map               arbitrary
;; set               arbitrary
;; treemap           by sort
;; treeset           by sort
;; index (datomic)   by sort

;;;; that's it for functions
















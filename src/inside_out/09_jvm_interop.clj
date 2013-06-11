(ns inside-out.09-jvm-interop)
(set! *print-length* 7)

;;;; Objective: Power
;; Leverage
;; Features
;; Performance
;; Reach

;;;; Crossing the Bridge

;;;; Apple Pie Preprocessor
;; Remove stickers from apple
;; Discards bad apples

;;;; Approaches
;;                    Imperative     map/filter     reduce/combine
;; composable?            no             yes            yes
;; mechanism          iteration      recursion      fn transformation
;; order              sequential     sequential     dunno (and I don't wanna no)
;; evaluation         eager          lazy           dunno
;; representation     places         sequential     dunno
;; parallelism        none           explicit       automatic

;; map . start a bunch of threads, leap-frog over eachother and do the work sequentially from the front

;;;; Map/Filter
(comment
  ;; The collection _apples_ is explicit
  (filter :edible? apples)
  (map #(dissoc % :sticker?) apples)
  (comp (partial filter :edible?)
        (partial map #(dissoc % :sticker?)))
)

;;;; Reduce/combine
(comment
  ;; collection vanishes
  (require '[clojure.core.reducers :as r]) ;; not in clojure 1.4

  (r/filter :edible?)
  (r/map #(dissoc % :sticker?))
  (comp (partial r/filter :edible?)
        (partial r/map #(dissoc % :sticker?)))
  ;; the shape of the code is completely unchanged!
)

;;;; Why is this in the interop module
;; Java has a library called fork/join, that has all the stuff we want

;; Leverage, Features, Performance, Reach
;;;; Translucent access to Fork/Join
;; Ordinary collections
;; Ordinary functions
;; Drop-in replacement Higher Order Functions 
;; 

;;;; Performance
;; table at 12.13
;; | Operation          | Apples  | Mean Time (msec) |
;; |--------------------+---------+------------------|
;; | filter-serial      | 1000000 |            63    |
;; | map-serial         | 1000000 |            98    |
;; | map-filter-serial  | 1000000 |           130    |
;; | filter-fold        | 1000000 |            10    |
;; | map-fold           | 1000000 |           110    |
;; | map-filter-fold    | 1000000 |            55    | <------- 2x speedup
;;
;; code at https://github.com/relevance/labrepl/master/test/labrepl/apple_pie_bench.clj

;;;; Unification
;; Clojure types _are_ platform types
;; Collections unify at interfaces (there are major languages on the jvm that does not do this!)
;; Primitives unify at implementation
;; Value types unify at implementation
;; (unify ==  not wrapped)

;; a) you can have great java interop because I look a lot like java
;; b) you can have great java interop because I don't wrap the java things

;;;; Platform Interfaces (Reach)
;; Create interfaces (not protocols) at interop boundaries
;; Must be implemented inline

;;;; Example: Datomic Entity

;; interface for non-Clojure JVM callers

;;    public interface Entity {
;;        Object get(Object key);
;;        Set keySet();                        
;;    }

;; and then, extending inline from clojure
;;   (deftype EntityMap [...]
;;     Entity
;;     ...)


;;;; Primitive Ops
;; Performance (unchecked ops)
;; Features (arbitrary precicion, silent overflow)

(def million 1000000)
(time (loop [i 0]
        (if (< i million)
          (recur (inc i))
          i)))

;;                  | unboxed arg |
(time (let [million (long million)]
        (loop [i 0]
          (if (< i million)
            (recur (inc i))
            i))))

;;;; Type Hinting
;; Platform performance
;; Some inference

;;;; Type Hinting Example

(defn capitalize
  "Uppercase the first character of a string,
lowercase the rest"
  [s]
  (if (.isEmpty s)
    s
    (let [up (.. s
                 (substring 0 1)
                 (toUpperCase))
          down (.. s
                   (substring 1)
                   (toLowerCase))]
      (.concat up down))))

(set! *warn-on-reflection* true)

(defn capitalize
  "Uppercase the first character of a string,
lowercase the rest"
  [^String s]  ;; <----- s is known to be a String
  (if (.isEmpty s)
    s
    (let [up (.. s
                 (substring 0 1)
                 (toUpperCase))
          down (.. s
                   (substring 1)
                   (toLowerCase))]
      (.concat up down))))


(defn capitalize
  "Uppercase the first character of a string,
lowercase the rest"
  [^String s]  ;; <----- s is known to be a String
  (if (.isEmpty s)
    s
    (.concat 
     (.toUpperCase (subs s 0 1))
     (.toLowerCase (subs s 1)))))

;;;; Numeric Options

(+ Long/MAX_VALUE Long/MAX_VALUE)

(binding [*unchecked-math* true]
  (eval '(+ Long/MAX_VALUE Long/MAX_VALUE)))

;; switch up to BigInteger math (slow)
(+' Long/MAX_VALUE Long/MAX_VALUE)

;; BigInteger math
(+ 9223372036854775807N 9223372036854775807N)

;; 21:50 Table with options

;;             speed     type      precision   overflow
;; checked     fast      any       limited     throws
;; promoting   slow      boxed     arbitrary   promotes
;; unchecked   fastest   unboxed   limited     silent

;;;; Array Operations

;;;; Accessing Arrays
(def nums (make-array Integer/TYPE 10))
(aset nums 4 1000)
(aget nums 4)
(seq nums)

;;;; contained type
(def nums (range 3))
(class (to-array nums))
(class (into-array nums))
(class (into-array Comparable nums))

;;;; Type information
(instance? Comparable "foobar")
(class "foobar")
(bases java.io.BufferedReader)
(supers java.io.BufferedReader)

;;;; reflection as data

(require '[clojure.reflect :as reflect])

(->> (reflect/reflect "String") ;; database lookup
     :members
     (filter #(= 'int (:return-type %))) ;; note the quote: types are names, not types
     (map :name)
     (into #{}))

;;;; Codec
;; 24:45
;; Your code is a database
;; Track definitions (like reflection)
;; Also track: use, history, provenance (who said it.. also called "blame")
;; In fact, run arbitrary analyzers
;; can work across libraries
;; and languages

;;;; A Codeq program
;; 27:41
;; Find all definitions of commit, and when each was created
(comment
  (d/q '[:find ?src (miin ?date)
         :in $ % ?name
         :where
         [?n :code/name ?name]
         [?cq :clj/def ?n]
         [?cq :codeq/code ?cs]
         [?cs :code/text ?src]
         [?cq :codeq/file ?f]
         (file-commits ?f ?c)
         (?c :commit/authoredAt ?date)]
       db rules "datomic.codeq.core/commit"))

;; http://block.datomic.com/2012/10/codeq.html

;;;; Interop
;; Not about syntax!

;;                         examples
;; leverage                vlaues, fork/join, codeq
;; features                unification, array ops, reflection
;; performance             unification, primitive ops, type hints, numeric options, array ops
;; reach                   unification, interfaces
;;                         clojure is the most interoperable non-java language on the jvm

;; questions .. 

;; the end
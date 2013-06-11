(ns inside-out.02-data)
(set! *print-length* 7)

;;;; <xml/>
;;;; json
;; there are no schemas in json, so there is no way of knowing what the data represents

;;;; edn - Extensible Data Notation
;; http://github.com/edn-format/edn

{:firsNname "John"
 :lastName "Smith"
 :age 25
 :address 
 {:streetAddres "21 2nd Street"
  :city "New York"
  :state "NY"
  :postalCode "10021"}
 :phoneNumber
 [{:type "name" :number "212 555-1234"}
  {:type "fax" :number "646 555-4567"}]}

;;;; edn characteristics
;; edn is a subset of Clojure syntax
;; used by Datomic and others as data transfer format
;; language/implememntation neutral
;; edn is a system for conveyance of _values_
;;   NOT a type system
;;   NOT schema based
;;   NOT a system for representing objects

;; edn does not have a schema, but it _does_ have a grammar

;; edn is a set of definitions for acceptable elements
;; no enclosing top element
;; it has all common data structures
;; rich set of build-ins
;; extension model


;;;; edn syntax

;;;; scalars
;; nil              nil, null or nothing
;; booleans         true or false
;; strings          enclosed in "double quotes" may span multiple lines \t \r \n supported
;; characters       \c \newline \return \space and \tab
;; integers         0-9 negative
;; floating point   64-bit (double) precision is expected

;;;; names
;; symbols          used to represent identifiers should map to something other than strings
;;                  may include _namespace_ prefixs: my-namespace/foo (symbols point to something else)
;; keywords         [not shown on screen at 7:35] (keywords name themselves)

;;;; collections
;; lists            a sequence of values, 
;;                  zero or more elements within ()
;;                  (a b 42)
;; vectors          a sequence of values 
;;                  .. that supports random access
;;                  zero or more elements within []
;;                  [a b 42]
;; maps             collection of key/value associations
;;                  every key should apper only once
;;                  unordered
;;                  zero or more elements within {}
;;                  {:a 1, "foo" :bar, [1 2 3] four}
;; sets             collection of unique
;;                  unordered
;;                  heterogeneous
;;                  zero or more elements within #{}
;;                  #{a b [1 2 3]}

;; everything nests

;;;; extensibility: tagged elements
;; starts with #
;; effect the semantics of the following element
;; allow development of custom tag handlers
;; recursively defined

;;;; Build-in Tagged Elements
;; #inst "rfc-3339-format"
;; An instant in time. The tagged element is a string in RFC-3339 format

;; #uuid "f81d4fae-7dec-11d0-a765-00a0c91ebf6"
;; A UUID. The tagged element is a canonical UUID string representation

;;;; disregard
;; comments          ;
;; discard           #_ is the discard sequence
;;                   read & discard the next element
;;                   [a b #_foo 42] => [a b 42]

;;;; equality
;; nil, booleans, strings, characters, and symbols are equal to values of the same type with the same edn representation.
(not= false nil)
(not= false "")
(not= nil "")

;; integers and floating point numbers should be consideret equal to values only of the same magnitude, type and precicion
(not= (int 0) (float 0) (long 0))

;; sequences (lists and vectors) are equal to other sequences whose count of element is the same, 
;; and for which each corresponding pari of elements (by ordinal) is equal
(= [0 1 2 3] '(0 1 2 3) (range 4))

;; sets are equal if they have the same count of elements and, for every element in one set, an equal element is in the other
(= #{0 1 2 3} #{3 2 1 0})

;; maps are equal if they have the same number of entries, and for every key/value entry in one map an equal key is present and mapped to an equal value in the other
(= {1 2 3 4} {3 4 1 2})

;; tagged elements must define their own equality semantics

;;;; the end
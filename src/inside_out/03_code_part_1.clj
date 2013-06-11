(ns inside-out.03-code-part-1)
(set! *print-length* 7)

;; clojure syntax is edn + ...

;;;; Functions

;; semantics: function call and an argument
(println "Hello World")

;; structure: a list, a symbol and a string

;; two different types for names
;; a symbol is a name that is intended to point to something else


;;;; Operators (no different than functions)


;; fn call and args
(+ 1 2 3 4)
;; a list with a symbol and some numbers, if seen from edn

;;;; Defining functions

;; defn Semantics

(defn greet                        ;; defin a fn and the fn name
  "Returns a friendly greeting"    ;; optional docstring
  [your-name]                      ;; arguments
  (str "Hello, " your-name))       ;; fn body
;; not CamelCase like Java .. lower-case,hyphenated which is traditional for lisp

;; defn Structure (edn)
(defn greet                        ;; a symbol and a symbol
  "Returns a friendly greeting"    ;; string
  [your-name]                      ;; vector
  (str "Hello, " your-name))       ;; list

;; traditional lisps use a nested list for function arguments. 
;; In Clojure a vector is used. This gives a visual clue in the function definitions and increses the readability.


;; Multi-arity
;; example: reduce

;;;; Metadata

;; Orthogonal to logical value of data
;; Available as map associated with symbol or collection
;; Does not impact equality of in an way intrude on value
;; Reader support
;; Not part of edn

;; meta-data is an open set

;;;; Metadata API

(def v [1 2 3])
(def trusted-v (with-meta v {:source :trusted}))

(:source (meta trusted-v))
(:source (meta v))

(= v trusted-v)

;;;; Metadata in the Reader

;; metadata on [1 2 3]

;; ^{:a 1 :b 2} [1 2 3]

;; ^String x .. special sugar for ^{:tag String} x
;; for the purpose of type hinting!

;;;; Metadata on Vars

(def
 ^{:arglists '([& items])
   :doc "Creates a new list containing the items."
   :added "1.0"}
  list (. clojure.lang.PersistentList creator))

;; where the hell does 'creator' come from? 
;; (. clojure.lang.PersistentList creator)

;; the 'var' is the thing that the symbol (list) points to, not the symbol itself
;; the var "list" itself, not the fn that "list" points to

(meta (var list))

;;;; Control flow

(defn larger [x y]
  (if (> x y) x y))

;; positive not strictly accurate ...
(defn larger-and-non-neg [x y]
  (if (> x y) 
    (if (pos? x) x 0)
    (if (pos? y) y 0)))

;; Refactor
(defn larger-and-non-neg 
  [x y]
  (larger 0 (larger x y)))

;; More Arities
(defn larger 
  ([x] x)
  ([x y] (if (> x y) x y))
  ([x y & more] (apply larger (larger x y ) more)))

;; the type of 'more' is a list. the & sign will 'roll' the arguments into a list
;; apply will 'unroll' the last argument .. when '& more' is encountered, apply should probably be used

(larger 1 3 42 3 53 23 32 )

;; Don't forget the one-arity (and zero-arity) version, if there it is sane to have it..

;; discover the fn we wanted already exists
(max -1 3 0)

;; and has hairy performance optimizations

;;(clojure.repl/source max)
(source max)

;;;; Namespace Declaration

;; (ns com.example.foo) ;; correspond to java packages, imply the same directory structure
(comment
  
  (ns com.example.foo
    (:require clojure.data.generators ;; <--- load some libs
              clojure.data.generative))

  (ns com.example.foo
    (:require [clojure.data.generators :as gen]   ;; <--- provide some short aliases for some libs 
              [clojure.data.generative :as test]))

  (ns com.example.foo
    (:require [clojure.data.generators :as gen]   ;; <--- unqualified access useful for "language extensions"
              [clojure.data.generative :refer (defspec)]))

  (ns ^{:author "Stuart Halloway"
        :doc "Data generators for Clojure."}
      clojure.data.generators
    (:refer-clojure :exclude [byte char long ...]) ;; <--- limit default aliaces of core namespace 
    (:require [clojure.core :as core]))            ;;      if you want to shadow core names

  ;; Don't do this
  ;; It's like import something.*; in java

  (ns com.example.foo
    (:use clojure.test.generatove)) ;; <---- makes all names unqualified
)

;;;; Language vs. API

;; import java.io.Closable;

;; java defines import syntax .. 

;; Class.forName("java.io.Closable"); can be used inside a function, but not where the import statemets are.

;; Java does not have a type for names, so we have to use a string to specify the name we are interested in

;; this is the version we put in a namespace declaration
;; (:require [clojure.string :as str])

;; the reflective version of this is:

;; (require '[clojure.string :as str])
;; clojure's interactive API build using language types (symbols, keywords, strings)

;; 29:14 "what's the single quote?"

;;;; def & var

(def answer 42)
(var answer)
#'answer

;;;; Vars

;; Shared root binding established by _def_
;; Root can be unbound
;; Vars established by _def_ are interned in namespace
;; functions stored in vars, so the too can be dynamically rebound

;;;; Namespaces

;; Uniquely (globally) named by simple (non qualified) symbol
;; Multi-segment names correspond to Java classpath naming - com.mycompany.ns
;; Think of Namespaces as ..
;;  Maps of simple symbols to Vars or Classes
;;  Maps of simple symbols to other namespaces (aliases)
;; Every symbol can have only one meaning per namespace

;;;; Namespaces (example) 

;; 33:00

(comment
  (def foo 42)
  (import 'java.util.Date)
  (ns bar)
  (def baz 17)
  (in-ns 'user)
  (refer 'bar)
  Date
  baz
  foo
  )

;;;; Dynamic Vars

;; have a initial _root_ binding
;; Can take on thread-local bindings
;;    and then be set!
;; Designated by ^:dynamic metadata
;; Oftent used for interactive settings

;;;; Var and Binding

(def ^:dynamic foo 0)
foo
(binding [foo 1] foo) ;; <--- change the binding of "foo" on this thread
foo

;; this is for out-of-band conveince of information, which is evil!

(set! *print-length* 3)

[1 2 3 4 5 6 7 8 9]
(iterate inc 1)      ;; <--- quite handy when collection is infinite

;; *earmufs* .. history

;;;; Common Dynamic Vars

;; Var                  Usage
;; *1 *2 *3             most recent 3 expression results
;; *e                   most recent expression
;; *compiler-options*   compiler options
;; *ns*                 current namespace
;; *in* *out* *err*     standard io streams
;; *data-readers*       edn support
;; *print-length*       length to print
;; *print-level*        depth to print



;; the end

;; the logo: integration iterop abstraction functions data code concurrency




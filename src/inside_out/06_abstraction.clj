(ns inside-out.06-abstractions)
(set! *print-length* 7)

;;;; Traditional OO
;; Objects are mutable
;; Encapsulate change and information
;; Polymorphism lives inside objects
;; Extend via inheritance
;; Interfaces are optional 
;;  (strongly suggested but not required)

;; Clojure pretty much rejects all these points, 
;; because every new type is a private language
;; with zero API support.
;; => Adapters everywhere
;; (which is happening in Java)

;;;; Clojure
;; Expose immutable data
;; Encapsulate _change_ (via constructors)
;; Polymorphism á la carte
;; Interfaces are mandatory
;; Extend by composition

;; (Java sees a shift towards extending by composition instead of extending via inheritance)

;;;; defrecord

;; it's your data!
;; (Maps can have names, but they are stil generically accessible)

;;;; From Maps ...

;; Data oriented
(def stu {:fname "Stu"
          :lname "Halloway"
          :address {:street "200 N Magnum"
                    :city "Durham"
                    :state "NC"
                    :zip 27001}})
;; keyword access
(:lname stu)
;; nested access
(-> stu :address :city)
;; update
(assoc stu :fname "Stuart")
;; nested update
(update-in stu [:address :zip] inc)

;;;; ....to Records

;; object oriented
(defrecord Person [fname lname address]) 
(defrecord Address [street city state zip])
(def stu (Person. "Stu" "Halloway"
                  (Address. "200 N Mangum"
                            "Durham"
                            "NC"
                            27701)))

;; still data-oriented! Everything works as before
(:lname stu)
(-> stu :address :city)
(assoc stu :fname "Stuart")
(update-in stu [:address :zip] inc)

;;;; defrecord

;; named type with slots
(defrecord Foo [a b c])
;; positional constructors
(def f (Foo. 1 2 3))
;;keyword access
(:b f)
;; plain old class
(class f)
;; casydht* (clojure abstracts so you don't have to)
(supers (class f))

;;;; defrecord Details
;; Type fields can be primitive
;; Value-based equality & hash
;; In-line method defs can inline
;; Keyword field lookups can inline
;; Protocols make interfaces*
;; add java Annotations if you want to*
;;  (* interop only)

;;;; Protocols (named sets of functions)
(defprotocol AProtocol
  "A doc string for AProtocol abstraction"
  (bar [a b] "bar docs")
  (baz [a] "baz docs"))

;; Named set of generic functions
;; Polymorphic on type of first argument
;; No implementation
;; Define _fn_s in the same namespace as protocols

;;;; Extending Protocols
;; (associating them with bits of implementation)
;; Extend Protocols Inline

(defrecord Bar [a b c]
  AProtocol
  (bar [this b] "Bar bar")
  (baz [this] (str "Bar baz " c)))
(def b (Bar. 5 6 7))
(baz b)

;;;; Extend Protocols Inline
;; (09:32)
(comment
  (defrecord BrowserEnv []
    repl/IJavaScriptEnv
    (-setup [this]
      (do (require 'cls.repl.reflect)
          (repl/analyze-source (:src this))
          (comp/with-core-cljs (server/start this))))
    (-evaluate [_ _ _ js] (browser-eval js))
    (-load [this ns url] (load-javascript this ns url))
    (-tear-down [_]
      (do (server/stop)
          (reset! server/state {})
          (reset! browser-state {})))))

;;;; Extending to a Type
;; (baz "a")
;; --> exception

(extend-type String
  AProtocol
  (bar [s s2] (str s s2))
  (baz [s] (str "baz " s)))

(baz "a")

;;;; Extending to many Types
;; (from Clojure reducers.clj 11:23)

;;;; Extending to Many Protocols
;; (from ClojureScript core.cljs 12:20)

;; Java is "complecting" .. braiding together things that you wish you hadn't
;; the opposite term is "simplifying" .. which is what Clojure does

;;;; Composition with Extend
;; (from Clojure java/io.clj 13:36)
(comment
  (extend BufferedOutputStream
    IOFactory
    (assoc default-streams-impl
      :make-output-stream (fn [x opts] x)
      :make-writer outputstream->writer))
)

;; the "DSL" for advanced reuse is maps and assoc

;;;; Reify 
(let [x 42
      r (reify AProtocol
          (bar [this b] "reify bar")
          (baz [this ] (str "reify baz " x)))]
  (baz r))

;; implements one or more protocols or interfaces and closes over environment like fn
;; Reify is good for Java interop and callbacks

;;;; Extention Options
;; Extend inline
;; Reify
;; Extend to _nil_
;; Extend multiple protocols: _extend-type_
;; Extend to multiple types: _extend-protocol_
;; At bottom, arbitrary _fn_ maps: _extend_


;;;; deftype

;; (16:33) divide the world in information and mechanism
;; in Java .. both information and mechanism is represented by classes
;; in Clojure ..
;; information != mechanism 
;; defrecord .. for information, because data belongs in maps!
;; deftype   .. for mechanism

;;;; deftype
;; still a named type with slots
(deftype Bar [a b c])
;; constructor, check
(def o (Bar. 1 2 3))
;; direct field access only
(.b o)
;; yoyo*
(supers (class o))

;; *you're on your own

;;;; The Other Constructor
(def f (Foo. 1 2 3 {:meta 1} {:extra 4}))
(meta f)
;; {:extra 4} is an extra k/v pair
(into {} f) 

;;;; deftype Example
;; (from ClojureScript core.cljs 19:28)

;;;; deftype Details
;; Type fields can be primitives
;; roll your own equality & hash
;; In'line method _def_s can inline
;; keyword field lookups can inline
;; protocols make interfaces*
;; add Java annotations*
;; Fields can be mutable+

;; *interop only
;; +experts only!

;;;; A trivial example rock/paper/scissors
;; http://rubyquiz.com/quiz16.html

;;;; A Player
(defprotocol Player
  (choose [p]) ; <--- pick :rock, :paper or :scissors
  (update-strategy [p me you])) ; <--- return an updated Player based on what you and I did

;;;; Stubborn Player
(defrecord Stubborn [choice]
  Player
  (choose [_] choice)
  (update-strategy [this _ _] this)) ; <--- never change

;;;; Mean Player
(comment
  (defn random-choice [] )
  (defn iwon? [me you] )
  (defrecord Mean [last-winner]
    Player
    (choose [_]
      (if last-winner
        last-winner
        (random-choice)))
    (update-strategy [_ me you]
      (Mean. (when (iwon? me you) me)))))


;;;; The Expression Problem
;; (23:59)
;; A should be able to work with B's abstractions, and vice versa, _without modification of the original code_
;; abstractions and concretions

;;;; A Can't Inherit from B
;; B is newer than A
;; A is hard to change
;; We don't control A
;; this happens within a single library

;;;; Some Approaches to the Expression Problem
;;;; 1. Roll-your-own
;; if/then instanceof? logic
;; closed
;; ugly example at 27:12, from the source of clojure
;;  written this way because there is no other choice

;;;; 2. Wrappers
;; (27:59)
;; NiftyString .. wrapper on string that I control .. 

;;;; Wrappers = Complexity
;; Ruin identity
;; Ruin equality
;; Cause non-local defects
;; Don't compose: AB + AC != ABC

;; AwesomeString .. 
;; Have bad names

;;;; 3. Monkey Patching (Duck punching)
;; (30:19) Common in e.g. Ruby, not possible in java

;; Monkey Patching = Complexity
;; Perserves identity, mostly
;; Ruins namespacing
;; Cause non-local defects
;; forbidden in most languages

;;;; 4. Generic Functions (CLOS)
;; polymorphism lives in the fns
;; don't touch existing implementation
;; Decouple polymorphism & types
;; Polymorphism in the fn's, not the types
;; no "isa" requirement
;; no type intrusion necessary

;; Protocols == generic functions 
;;              - arbitrary dispatch
;;              + speed
;;              + grouping
;; (and still powerful enough to solve the expression problem!)

;; Clojure solves the problem with another construction as well: multimethods!

;;;; the end



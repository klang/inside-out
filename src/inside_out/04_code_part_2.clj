(ns inside-out.04-code-part-2)
(set! *print-length* 7)

(comment
  ;; new Widget("red")
  (Widget. "red")

  ;; static members
  ;; Math.PI
  (Math/PI)

  ;; instance members
  ;; rnd.nextInt()
  (.nextInt rnd)

  ;; in clojure, the verb always comes first.
  ;; (the operator comes first)

  ;; chaining access
  ;; person.getAddress().getZipCode()

  (.. person getAddress getZipCode)

  ;; Clojure has less paranthesis than Java!

  ;; all forms are created equal!

  ;; form          syntax example
  ;; function      list   (println "hello")
  ;; operator      list   (+ 1 2)
  ;; method call   list   (.trim "hello")
  ;; import        list   (require 'mylib)
  ;; metadata      list   (with-meta obj m)
  ;; control flow  list   (when valid? (proceed))
  ;; scope         list   (dosync (alter ...))

  ;; all lead with the verb .. 
)


;;;; Destructoring

;; Pervasive Destructuring
;; DSL for binding names
;; Works with abstract structure
;; Available whenever names are made*
;; Vector binding forms destructure _sequential_ things
;; Map binding forms destructure _associative_ things

;; Example of Sequential Destructoring

(defn next-fib-pair
  [pair]
  [(second pair) (+ (first pair) (second pair))])

(iterate next-fib-pair [0 1])

;; the function is dominated by the code that picks the data apart, making it hard to figure out what it does.

;; ---

(defn next-fib-pair
  [[a b]]
  [b (+ a b)])

(iterate next-fib-pair [0 1])

;; because this is so compact, we tend to do this in-line

;; ---

(iterate (fn [[a b]] [b (+ a b)]) [0 1])

;; ---

(defn fibs [] (map first (iterate (fn [[a b]] [b (+ a b)]) [0 1])))

(fibs)

;; that was sequential destructoring

;; Example of Associative destructoring
(require '[clojure.string :as str :only (join)])

(defn format-name
  [person]
  (str/join " " [(:salutation person)
                 (:first-name person)
                 (:last-name person)]))

(format-name {:salutation "Mr." :first-name "John" :last-name "Doe"})

;; again, the code is dominated by picking apart 'person'

(defn format-name
  [name]
  (let [{salutation :salutation
         first-name :first-name
         last-name :last-name} 
        name]
    (str/join " " [salutation first-name last-name])))

(format-name {:salutation "Mr." :first-name "John" :last-name "Doe"})

;; ----

(defn format-name
  [{:keys [salutation first-name last-name]}]
  (str/join " " [salutation first-name last-name]))

(format-name {:salutation "Mr." :first-name "John" :last-name "Doe"})

;; ----
;; Optional Keyword Args

(defn game
  [planet & {:keys [human-players computer-players]}]
  (println "Total players:"
           (+ human-players computer-players)))

(game "Mars" :human-players 1 :computer-players 2)
(game "Mars" :computer-players 2 :human-players 1 )

;; NOTE! Clojure does NOT have a feature that allows optional keyword args.
;; it's simply a consequence of variable arity fns plus map destructuring!

;; ----
;; Desturcturing (examples)

(let [[a b c & d :as e] [1 2 3 4 5 6 7]]
  [a b c d e])

(let [[[x1 y1] [x2 y2]] [[1 2] [3 4]]] [x1 y1 x2 y2])

;; go easy on nesting destructuring .. it can get messy
;; error at 13:30 .. [a b c d m] should be [a b c m]
;;                   d is not defined here
(let [{a :a 
       b :b 
       c :c :as m 
       :or {a 2 b 3}} {:a 5 :c 6}] [a b c m])


(let [{:keys [a b c]} {:a 5 :c 6}] [a b c])

;;;; Sugar == Reader Macros
;; These examples only show the result of _reading_ a sugared form, not evaluating it

;; 'foo translates into (quote foo)
;; @something translates into (deref something)
;; anonymous function # followed by ( .i.e. #(+ foo %) 
;; #'foo becomes var
;; discard #_foo 
;; unreadable #<
;; eval #= .. should not be used

;;;; Special forms

(comment
  (def symbol init?)
  (if test then else?)
  (do exprs*)
  (quote form)
  (fn name? [params*] exprs*)
  (fn name? ( [params*] exprs*))
  (let [bindings*] exprs*)
  (loop [bindings*] exprs*)
  (recur exprs*)
  (throw expr)
  (try expr catch-clause* finally-clause?)

  )


;;;; Macros
;; Programs writing Programs

;; Code Text --->
;;                Reader  ---> data structures --->
;; You       --->                                   evaluator/ompiler ---> bytecode ---> JVM --- effect
;;                Program ---> data structures --->    |        ^
;;                                                     |        | 
;;                                                   data structures
;;                                                     |        | 
;;                                                     v        |
;;                                                    Program(macro) 

;; Inside Out?

{:name "Jonathan"}

;; ----

(assoc {:name "Jonathan"} :nickname "Jon")

;; ----

(dissoc
 (assoc {:name "Jonathan" :password "secret"} 
   :nickname "Jon")
 :password)

;; things get stacked up..

;; ---
;; Thread First ->

(-> {:name "Jonathan" :password "secret"} 
    (assoc :nickname "Jon")
    (dissoc :password))

;; the transformation from Thread First notation to inside out notation happens _before_ the program runs.
;; the -> macro runs first, transforming the forms into something that is compiled to bytecode

;; the .. macro works in the exact same way!

;; Annotations? (25:23)
;; .. makes a ton more work for Java devlopers .. 
;; that's why we have so many Java developers .. there is a lot of work to be done

;;;; seq ops inside out

;; sequences are always passed as the last argument
;; (objects are always passed as the first argument)

(range 10)

(map inc (range 10))

(filter odd? (map inc (range 10)))

(reduce + (filter odd? (map inc (range 10))))

;; Thread Last ->>

(->> (range 10)
     (map inc)
     (filter odd?)
     (reduce +))

;;;; Example

;; isBlank() from Apache Commons (static method that returns a boolean)
;; 
;; public class StringUtils {
;;   public static boolean isBlank(String str) {
;;     int strLen;
;;     if (str == null || (strlen = str.length()) == 0) {
;;       return true;
;;     }
;;     for (int i = 0; i < strLen; i++) {
;;       if ((Character.isWhitespace(str.charAt(i)) == false)) {
;;         return false;
;;       }
;;     }
;;     return true;
;;   }
;; }

;; remove type declarations

;; public class StringUtils {
;;   public isBlank(str) {
;;     if (str == null || (strlen = str.length()) == 0) {
;;       return true;
;;     }
;;     for (int i = 0; i < strLen; i++) {
;;       if ((Character.isWhitespace(str.charAt(i)) == false)) {
;;         return false;
;;       }
;;     }
;;     return true;
;;   }
;; }

;; remove the class wrapper

;;   public isBlank(str) {
;;     int strLen;
;;     if (str == null || (strlen = str.length()) == 0) {
;;       return true;
;;     }
;;     for (int i = 0; i < strLen; i++) {
;;       if ((Character.isWhitespace(str.charAt(i)) == false)) {
;;         return false;
;;       }
;;     }
;;     return true;
;;   }



;; higher-order fuction 'every'

;;   public isBlank(str) {
;;     if (str == null || (strlen = str.length()) == 0) {
;;       return true;
;;     }
;;     every (ch in str) {
;;       Character.isWhitespace(ch);
;;       }
;;     }
;;     return true;
;;   }

;; get rid of corner cases

;;   public isBlank(str) {
;;     every (ch in str) {
;;       Character.isWhitespace(ch);
;;       }
;;     }
;;   }

;; lispify!

(defn blank? [s] (every? #(Character/isWhitespace %) s))

;;;; when does verbosity drive obscurity?


;; The reduction in lines of code widens with scale:
;; Small Clojure programs tend to be 10 times shorter.
;; Big Clojure programs tend to be 100 times shorter.
;; 
;; System maintainance cost raise linearily with codebase, so when a language only needs a tenth of the lines, 
;; it has to be taken seriously

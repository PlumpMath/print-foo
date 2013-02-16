(ns print-foo.print-foo
  "Macros for printing diagnostic information while debugging or developing."
  (:require [gui-diff.core :as gui-diff]))


(defn- print-and-return [& xs]
  (when (seq (butlast xs))
    (print (apply str (butlast xs))))
  (gui-diff/p (last xs))
  (last xs))

(defmacro print->
  "Diagnostic tool for printing the values at each step of a `->`"
  [& body]
  (let [print-forms (map #(list `(fn [x#] (#'print-and-return ~% "-> " x#))) (range))]
    (cons '-> (interleave body print-forms))))

(defmacro print->>
  "Diagnostic tool for printing the values at each step of a `->>`"
  [& body]
  (let [print-forms (map #(list `#'print-and-return % "->> ") (range))]
    (cons '->> (interleave body print-forms))))

(defmacro print-let
  "Diagnostic tool for printing the values at each step of a `let`"
  [bindings & body]
  (let [firsts (take-nth 2 bindings)
        seconds (take-nth 2 (rest bindings))]
    `(let ~(vec (interleave firsts
                            (map (fn [lhs rhs]
                                   `(foo '~lhs " " ~rhs))
                                 firsts
                                 seconds)))
       (#'print-and-return "let-body-value " (do ~@body)))))

(defmacro print-if [test expr1 expr2]
  `(if (#'print-and-return '~test " " ~test)
     (#'print-and-return '~expr1 " " ~expr1)
     (#'print-and-return '~expr2 " " ~expr2)))

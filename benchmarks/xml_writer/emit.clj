(ns xml-writer.emit
  (:require [perforate.core :refer :all]
            [xml-writer.core :as xml]
            [clojure.data.xml :as dxml]))


(defn gen-sexp [n form]
  [:root {:attr "value"}
   (doall (repeat n form))])

(defn gen-nested [x y]
  (gen-sexp x (take y (iterate (partial vector :inner {:x 1 :y 20.1M}) [:bar "bar"]))))

(def input-10k-children (gen-sexp 10000 [:child {:a 1 :b 20.1M :c nil}]))
(def input-nested (gen-sexp 100 (take 50 (iterate (partial vector :inner {:x 1 :y 20.1M}) [:bar "bar"]))))

(defgoal emit-xml "Emit XML")

(defcase emit-xml :xml-writer-10k []
  (xml/emit-sexp-str input-10k-children))

(defcase emit-xml :clojure-data-xml-10k []
  (dxml/emit-str (dxml/sexp-as-element input-10k-children)))

(defcase emit-xml :xml-writer-nested []
  (xml/emit-sexp-str input-nested))

(defcase emit-xml :clojure-data-xml-nested []
  (dxml/emit-str (dxml/sexp-as-element input-nested)))

(comment
  (= (xml/emit-sexp-str (gen-sexp 10 [:child {:a nil} "value"]))
     (dxml/emit-str (dxml/sexp-as-element (gen-sexp 10 [:child {:a nil} "value"]))))

  (= (xml/emit-sexp-str (gen-nested 100 50))
     (dxml/emit-str (dxml/sexp-as-element (gen-nested 100 50)))))

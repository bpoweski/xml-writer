(ns xml-writer.core-test
  (:require [midje.sweet :refer :all]
            [xml-writer.core :refer :all]
            [clojure.data.xml :as xml]))


(defn data-xml [sexp]
  (xml/emit-str (xml/sexp-as-element sexp)))

(def compatible? (chatty-checker [sexp] (= (xml/parse-str (emit-sexp-str sexp)) (xml/parse-str (data-xml sexp)))))

(facts "emitting one element"
  [:root] => compatible?
  [:root {}] => compatible?
  [:root {} nil] => compatible?
  [:root "body"] => compatible?)

(facts "emitting prefixed elements"
  [:env:Envelope {:xmlns:env "http://schemas.xmlsoap.org/soap/envelope/"}] => compatible?
  [:env:Envelope {:xmlns:env "http://schemas.xmlsoap.org/soap/envelope/"} nil] => compatible?)

(facts "with attributes"
  [:root {:a 1}] => compatible?
  [:root {:a 1} nil] => compatible?)

(facts "emitting child elements"
  [:root [:child {} "foo"]] => compatible?
  [:root [:child {} "first"] [:child {} "second"]] => compatible?)

(facts "emitting object values"
  [:root 20.0M] => compatible?)

(facts "emitting xml string despite invalid input characters"
  (emit-sexp-str
   [:root [:child {:attribute "value"} "Some text\u0000more text"]]
   :invalid-char-behavior :replace-all :replacement-char (char \;)) =>
  "<?xml version='1.0' encoding='UTF-8'?><root><child attribute=\"value\">Some text;more text</child></root>"
  (emit-sexp-str
   [:root [:child {:attribute "value"} "Some text\u0001more text"]]
   :invalid-char-behavior :replace-whitespace) =>
  "<?xml version='1.0' encoding='UTF-8'?><root><child attribute=\"value\">Some text more text</child></root>")

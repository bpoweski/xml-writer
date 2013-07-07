(ns ^{:doc "Functions to emit XML using the XMLStreamWriter interface."
      :author "Ben Poweski"}
  xml-writer.core
  (:require [clojure.pprint :refer [pprint]])
  (:import (javax.xml.stream XMLStreamWriter XMLOutputFactory)
           (java.io StringWriter Writer)))


(defprotocol EmitXML
  (emit! [sexp writer]))

(defn emit-attributes! [attrs ^XMLStreamWriter writer]
  (doseq [[k v] attrs]
    (.writeAttribute writer (name k) (str v))))

(extend-protocol EmitXML
  clojure.lang.IPersistentVector
  (emit! [v ^XMLStreamWriter writer]
    (let [[elem & [attrs & more]] v]
      (.writeStartElement writer "" (name elem) "")
      (if (map? attrs)
        (do (emit-attributes! attrs writer)
            (emit! more writer))
        (do (emit! attrs writer)
            (emit! more writer)))
      (.writeEndElement writer)))

  clojure.lang.ISeq
  (emit! [coll writer]
    (doseq [e coll]
      (emit! e writer)))

  java.lang.String
  (emit! [s ^XMLStreamWriter writer] (.writeCharacters writer s))

  nil
  (emit! [_ _])

  java.lang.Object
  (emit! [o ^XMLStreamWriter writer] (.writeCharacters writer (str o))))

(defn encode-sexp [sexp ^Writer writer & {:as opts}]
  "Encodes a given sexp into a given Writer."
  (let [^XMLStreamWriter writer (-> (XMLOutputFactory/newInstance)
                                    (.createXMLStreamWriter writer))]
    (.writeStartDocument writer (or (:encoding opts) "UTF-8") "1.0")
    (emit! sexp writer)
    (.writeEndDocument writer)
    writer))

(defn emit-sexp-str
  "Emits XML generated from the given sexp.  Returns results as a String."
  [sexp]
  (let [^StringWriter writer (StringWriter.)]
    (encode-sexp sexp writer)
    (.toString writer)))


(ns ^{:doc "Functions to emit XML using the XMLStreamWriter interface."
      :author "Ben Poweski"}
    xml-writer.core
  (:require [clojure.pprint :refer [pprint]]
            [clojure.data.xml :as xml]
            [clojure.string :as str])
  (:import (javax.xml.stream XMLStreamWriter XMLOutputFactory)
           (java.io StringWriter Writer)
           (com.ctc.wstx.api InvalidCharHandler$ReplacingHandler InvalidCharHandler$FailingHandler)
           (com.github.bpoweski ReplaceInvalidWhitespace)))


(set! *warn-on-reflection* true)

(defprotocol EmitXML
  (emit! [sexp writer]))

(defn qualified-name [event-name]
  (if (instance? clojure.lang.Named event-name)
    [(namespace event-name) (name event-name)]
    (let [name-parts (str/split event-name #"/" 2)]
      (if (= 2 (count name-parts))
        name-parts
        [nil (first name-parts)]))))

(defn emit-attributes! [attrs ^XMLStreamWriter writer]
  (doseq [[k v] attrs]
    (let [[attr-ns attr-name] (qualified-name k)]
      (if attr-ns
        (.writeAttribute writer attr-ns attr-name (str v))
        (.writeAttribute writer attr-name (str v))))))

(extend-protocol EmitXML
  clojure.lang.IPersistentVector
  (emit! [v ^XMLStreamWriter writer]
    (let [[elem & [attrs & more]] v
          [nspace qname] (qualified-name elem)]
      (.writeStartElement writer "" qname (or nspace ""))
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


(defn set-woodstox-properties [{:keys [invalid-char-behavior replacement-char]}]
  (let [invalid-char-handler (case invalid-char-behavior
                               :replace-all        (InvalidCharHandler$ReplacingHandler. (or replacement-char (char \space)))
                               :replace-whitespace (new ReplaceInvalidWhitespace)
                               :fail               (InvalidCharHandler$FailingHandler/getInstance)
                               (InvalidCharHandler$FailingHandler/getInstance))]
    (doto (XMLOutputFactory/newInstance)
      (.setProperty com.ctc.wstx.api.WstxOutputProperties/P_AUTOMATIC_END_ELEMENTS false)
      (.setProperty com.ctc.wstx.api.WstxOutputProperties/P_OUTPUT_INVALID_CHAR_HANDLER invalid-char-handler))))

(defn encode-sexp
  "Encodes a given sexp into a given Writer."
  [sexp ^Writer writer & {:as opts}]
  (let [factory                 (set-woodstox-properties opts)
        ^XMLStreamWriter writer (.createXMLStreamWriter factory writer)]
    (.writeStartDocument writer (or (:encoding opts) "UTF-8") "1.0")
    (emit! sexp writer)
    (.writeEndDocument writer)
    writer))

(defn emit-sexp-str
  "Emits XML generated from the given sexp.  Returns results as a String."
  [sexp & rest]
  (let [^StringWriter writer (StringWriter.)]
    (apply encode-sexp sexp writer rest)
    (.toString writer)))

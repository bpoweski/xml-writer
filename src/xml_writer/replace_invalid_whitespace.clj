(ns xml-writer.replace-invalid-whitespace
  (:import (com.ctc.wstx.api InvalidCharHandler$ReplacingHandler)
           (com.ctc.wstx.api InvalidCharHandler$FailingHandler))
  (:gen-class
   :name com.github.bpoweski.ReplaceInvalidWhitespace
   :implements [com.ctc.wstx.api.InvalidCharHandler]))

(defn invalid-whitespace-char?
  "to be used in cases where invalid whitespace characters should be replaced, but the user 
  would like to throw an exception for all other conditions of FailingHandler. see:
  https://github.com/FasterXML/woodstox/blob/7aa010e672d1bb7d81b4650a71705cb2bb524f98/src/main/java/com/ctc/wstx/api/InvalidCharHandler.java#L54"
  [c]
  (or (and (< c (int \space)) (not= c 0))
      (and (>= c 0x7F)
           (<= c 0x9F))))

(let [failing-handler (InvalidCharHandler$FailingHandler/getInstance)]
  (defn -convertInvalidChar [this c]
    (if (invalid-whitespace-char? c)
      (char \space)
      (.convertInvalidChar failing-handler c))))

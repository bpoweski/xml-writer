(defproject xml-writer "0.1.0"
  :description "Fast-ish XML serialization"
  :url "http://github.com/bpoweski/xml-writer"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :profiles {:dev {:dependencies [[midje "1.5.1"]
                                  [org.clojure/data.xml "0.0.6"]
                                  [perforate "0.3.2"]]
                   :plugins [[lein-midje "2.0.3"]
                             [perforate "0.3.2"]]}
             :bench {:jvm-opts  ^:replace []}}
  :perforate {:environments [{:name :current
                              :profiles [:dev :bench]
                              :namespaces [xml-writer.emit]}]})

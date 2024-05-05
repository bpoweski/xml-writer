(defproject xml-writer "0.2.0"
  :description "Fast-ish XML serialization"
  :url "http://github.com/bpoweski/xml-writer"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [com.fasterxml.woodstox/woodstox-core "5.1.0"]
                 [org.clojure/data.xml "0.0.8"]]
  :profiles {:dev {:dependencies [[midje "1.10.10"]
                                  [perforate "0.3.4"]]
                   :plugins [[lein-midje "3.1.1"]
                             [perforate "0.3.4"]]}
             :bench {:jvm-opts  ^:replace []}}
  :perforate {:environments [{:name :current
                              :profiles [:dev :bench]
                              :namespaces [xml-writer.room-avail]}]}
  :aot [xml-writer.replace-invalid-whitespace]
  :java-source-paths ["src"]
  :repositories [["sonatype-oss-public" "https://oss.sonatype.org/content/groups/public/"]])

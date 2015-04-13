(defproject edeposit.amqp.pdfbox "0.1.0"
  :description "PDFBOX wrapper for AMQP"
  :url "http://github.com/edeposit/edeposit.amqp.pdfbox"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/data.xml "0.0.8"]
                 [org.apache.pdfbox/pdfbox "1.8.3"]
                 [org.apache.pdfbox/preflight "1.8.3"]
                 [org.clojure/tools.cli "0.3.1"]
                 [org.clojure/data.zip "0.1.1"]
                 [com.novemberain/langohr "3.0.1"]
                 [clj-time "0.8.0"]
                 [org.clojure/data.json "0.2.5"]
                 [environ "1.0.0"]
                 [reloaded.repl "0.1.0"]
                 [me.raynes/fs "1.4.6"]
                 [org.clojure/tools.nrepl "0.2.7"]
                 [org.clojure/tools.logging "0.3.1"]
                 [commons-codec/commons-codec "1.10"]
                 [org.apache.commons/commons-lang3 "3.4"]
                 ]
  :main edeposit.amqp.pdfbox.main
  :aot [edeposit.amqp.pdfbox.main clojure.tools.logging.impl]
  :profiles {:dev {:plugins [
                             [quickie "0.3.6"]
                             [spyscope "0.1.5"]
                             [lein-ubersource "0.1.1"]
                             ]}
             :uberjar {:aot :all}
             }
  )

(defproject edeposit.amqp.pdfbox "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/data.xml "0.0.7"]
                 [org.apache.pdfbox/pdfbox "1.8.3"]
                 [org.apache.pdfbox/preflight "1.8.3"]
                 [org.clojure/tools.cli "0.2.4"]
                 [org.clojure/data.zip "0.1.1"]
                 [com.novemberain/langohr "1.4.1"]
                 ]
  :profiles {:dev {:plugins [[com.jakemccrary/lein-test-refresh "0.1.2"]]}}
  )

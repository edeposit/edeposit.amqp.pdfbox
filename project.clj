(defproject edeposit.amqp.pdfbox "0.1.0-SNAPSHOT"
  :description "AMQP middleware for validation, identification PDF using PDFBox"
  :url "https://github.com/jstavel/edeposit.amqp.pdfbox"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.apache.pdfbox/pdfbox "1.8.2"]
                 [org.apache.pdfbox/preflight "1.8.2"]
                 [org.clojure/data.xml "0.0.7"]
                 [org.clojure/tools.cli "0.2.4"]
                 [com.novemberain/langohr "1.4.1"]
                 ]
  :main edeposit.amqp.pdfbox.core
  )

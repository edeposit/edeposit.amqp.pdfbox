(ns edeposit.amqp.pdfbox.main
  (:require 
   [clojure.pprint :as pp]
   [clojure.data.xml :as xml]
   [edeposit.amqp.pdfbox.utils :as utils]
   [clojure.tools.cli :as cli]
   [clj-time.format :as format]
   [edeposit.amqp.pdfbox.systems :refer [prod-system]]
   [reloaded.repl :refer [system init start stop go reset]]
   [edeposit.amqp.pdfbox.core :refer [validate]]
   [clojure.java.io :as io]
   [clojure.tools.nrepl.server :refer (start-server)]
   )
  (:gen-class :main true)
  )

(defn -main [& args]
  (let [ [options args banner] 
         (cli/cli args
                  [ "-f" "--file"]
                  [ "--amqp"      :default false :flag true]
                  [ "-h" "--help" :default false :flag true]
                  )
         ]
    (when (:help options)
      (println banner)
      (System/exit 0)
      )
    (when (:amqp options)
      (defonce server (start-server :port 12345))
      (reloaded.repl/set-init! prod-system)
      (go)
      )
    (when (:file options)
      (println (xml/indent-str (validate (io/file (:file options)))))
      )
    )
  )

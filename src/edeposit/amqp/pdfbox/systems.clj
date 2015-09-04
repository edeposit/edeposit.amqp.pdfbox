(ns edeposit.amqp.pdfbox.systems
  (:require [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [edeposit.amqp.pdfbox.components :refer [new-pdfbox-amqp]]))

(defn prod-system []
  (component/system-map
   :pdfbox-amqp (new-pdfbox-amqp
                    (env :pdfbox-amqp-uri)
                    (env :pdfbox-amqp-exchange) 
                    (env :pdfbox-amqp-qname)
                    (env :pdfbox-debug)
                    )
   )
  )

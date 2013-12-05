(ns edeposit.amqp.pdfbox.amqp
  (:require [clojure.java.io :as io]
            [clojure.data.zip.xml :as xml]
            [clojure.zip :as zip]
            [edeposit.amqp.pdfbox.utils :as utils]
            [edeposit.amqp.pdfbox.core :as core]
            [clojure.data.json :as json]
            [clojure.data.xml :as x]
            [clojure.data.codec.base64 :as b64]
            [langohr.core      :as rmq]
            [langohr.channel   :as lch]
            [langohr.queue     :as lq]
            [langohr.consumers :as lc]
            [langohr.basic     :as lb]
            )
  (:import [org.apache.pdfbox.pdmodel PDDocument PDPage PDDocumentInformation]
           [org.apache.pdfbox.pdmodel.edit PDPageContentStream]
           [org.apache.pdfbox.pdmodel.font PDType1Font]
           [org.apache.pdfbox.preflight.parser PreflightParser]
           [org.apache.pdfbox.preflight PreflightDocument]
           [java.util Date]
           [java.util UUID]
           [com.rabbitmq.client ConnectionFactory]
           )
  )

(defn makeDataWithUrl [file]
  (let [msg
        {:uuid (-> (UUID/randomUUID) str)
         :url (-> file .getAbsolutePath)
         }
        ]
    msg
    )
  )

(defn to-base64 [string]
  (java.lang.String. (b64/encode (.getBytes string)))
  )

(def metadata-serialize (comp to-base64 x/indent-str zip/xml-zip))

(defn makeDataWithValidationResult [related-data & {:keys [is-valid meta errors]
                                                    :or [:is-valid true :meta "" :errors []]
                                                    }]
  (let [response  {:uuid (str (UUID/randomUUID))
                   :relatedUUID (:uuid related-data)
                   :isValid is-valid
                   :errors errors
                   :metadata (metadata-serialize meta)}
        ]
    response
    )
  )

(defn processDataWithUrl [data]
  (let [fname (:url data)
        metadata (core/xmlValidationOutput (io/file fname))
        xmldata (zip/xml-zip metadata)
        is-valid (first (xml/xml-> xmldata :validation :isValidPDFA xml/text))
        errors (xml/xml-> xmldata :validation :validationErrors :error first xml/text)
        ]
    ;(println  (xml/xml-> xmldata :validation :validationErrors xml/text))
    (makeDataWithValidationResult data :is-valid is-valid :meta metadata)
    )
  )

(def ^{:dynamic true :doc "Setting of amqp sender for PDFBox."}
  *pdfbox-config* {:username "guest"
                   :password "guest"
                   :vhost     "/pdf"
                   :host      "localhost"
                   :port      ConnectionFactory/DEFAULT_AMQP_PORT
                   })

(defn sendData [data]
  (let [conn (rmq/connect *pdfbox-config*)]
      )
  (lq/declare )
  )

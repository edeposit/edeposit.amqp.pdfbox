(ns edeposit.amqp.pdfbox.handlers
  (:require [clojure.java.io :as io]
            [clojure.data.zip.xml :as xml]
            [clojure.zip :as zip]
            [edeposit.amqp.pdfbox.core :as core]
            [clojure.data.json :as json]
            [clojure.data.xml :as x]
            [langohr.basic     :as lb]
            [me.raynes.fs :as fs]
            )
  (:import [org.apache.commons.codec.binary Base64])
  )

(comment ;; hook for emacs
  (add-hook 'after-save-hook 'restart-app nil t)
)

;; (defn to-base64 [string]
;;   (java.lang.String. (base64/encode (.getBytes string)))
;;   )

(defn metadata-serialize [xml] 
  (-> xml
      zip/xml-zip
      x/indent-str
      ;; to-base64
      (.getBytes)
      ;base64/encode
      (java.lang.String.)
      )
  )

(comment
  (def metadata (read-string (slurp "resources/request-metadata.clj")))
  (def payload (.getBytes (slurp "resources/request-payload.bin")))
  (def msg (json/read-str (String. payload) :key-fn keyword))
  (with-open [out (fs/output-stream "/tmp/aa.pdf")]
    (.write out (Base64/decodeBase64 (:b64_data msg)))
    )
  )

(defn parse-and-validate [metadata ^bytes payload]
  (let [msg (json/read-str (String. payload) :key-fn keyword) 
        data-file (fs/temp-file "pdfbox-amqp-" ".pdf")
        ]
    (with-open [out (io/output-stream data-file)]
      (.write out (Base64/decodeBase64 (:b64_data msg)))
      )
    (let [result (core/validate data-file)]
      result
      )
    )
  )

(defn handle-delivery [ch exchange metadata ^bytes payload]
  (println "new message arrived")
  (defn send-response [msg]
    (lb/publish ch exchange "response" msg 
                {:UUID (:UUID metadata)
                 :content-type "edeposit/pdfbox-response"
                 :content-encoding "text/xml; charset=\"utf-8\""
                 }
                )
    )
  (-> (parse-and-validate metadata payload)
      (x/indent-str)
      (send-response)
      )
  (lb/ack ch (:delivery-tag metadata))
  )


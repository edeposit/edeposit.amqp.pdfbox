(ns edeposit.amqp.pdfbox.handlers
  (:require [clojure.java.io :as io]
            [clojure.data.zip.xml :as xml]
            [clojure.zip :as zip]
            [edeposit.amqp.pdfbox.core :as core]
            [clojure.data.json :as json]
            [clojure.data.xml :as x]
            [clojure.data.codec.base64 :as base64]
            [langohr.basic     :as lb]
            [me.raynes.fs :as fs]
            [byte-streams :as bs]
            )
  )

(comment ;; hook for emacs
  (add-hook 'after-save-hook 'restart-app nil t)
)

(defn to-base64 [string]
  (java.lang.String. (base64/encode (.getBytes string)))
  )

(defn metadata-serialize [xml] 
  (-> xml
      zip/xml-zip
      x/indent-str
      ;; to-base64
      (.getBytes)
      base64/encode
      (java.lang.String.)
      )
  )

(defn parse-and-validate [metadata ^bytes payload]
  (let [ msg (json/read-str (String. payload) :key-fn keyword) ]
    (let  [tmp-dir (fs/temp-dir "pdfbox-handle-delivery-validate-with-file-")
           b64data-file (io/file tmp-dir "data.base64")
           data-file (io/file tmp-dir "data.pdf")
           validate-result (io/file tmp-dir "validate-result.json") ]
      (bs/transfer (:b64_data msg) b64data-file)
      (with-open [in (io/input-stream b64data-file)
                  out (io/output-stream data-file) ]
        (base64/decoding-transfer in out))
      (let [result (core/validate data-file)]
        ;(fs/delete-dir tmp-dir)
        (assoc result :filename (:filename msg))
        )
      )
    )
  )

(defn handle-delivery [ch exchange metadata ^bytes payload]
  (println "new message arrived")
  (defn send-response [msg]
    (lb/publish ch exchange "response" msg 
                {:UUID (:UUID metadata)
                 :content-type "edeposit/pdfbox-response"
                 :content-encoding "application/json"
                 }
                )
    )
  (parse-and-validate metadata payload)
  ;; (-> (parse-and-validate metadata payload)
  ;;     (json/write-str)
  ;;     ;(send-response)
  ;;     )
  (lb/ack ch (:delivery-tag metadata))
  )


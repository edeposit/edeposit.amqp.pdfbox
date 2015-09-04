(ns edeposit.amqp.pdfbox.handlers
  (:require [clojure.java.io :as io]
            [clojure.data.zip.xml :as xml]
            [clojure.zip :as zip]
            [edeposit.amqp.pdfbox.core :as core]
            [clojure.data.json :as json]
            [clojure.data.xml :as x]
            [langohr.basic     :as lb]
            [me.raynes.fs :as fs]
            [clojure.tools.logging :as log]
            [clj-time.core :as time]
            [clj-time.coerce :as tc]
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
  (log/debug "parse-and-validate")
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

(defn response-properties [metadata]
  {:headers {"UUID" (-> metadata :headers (get "UUID"))}
   :content-type "edeposit/pdfbox-response"
   :content-encoding "text/xml; charset=\"utf-8\""
   :persistent true
   }
  )

(defn handle-delivery [ch exchange metadata ^bytes payload & {:keys [debug] :or {debug false}}]
  (log/info "new message arrived")
  (when debug
    (let [now-long (tc/to-long (time/now))]
      (let [fname (format "/tmp/pdfbox-%d-request-metadata.clj" now-long)]
        (log/info "write metadata to:" fname)
        (with-open [w (java.io.FileWriter. fname)]
          (print-dup metadata w)))
      (let [fname (format "/tmp/pdfbox-%d-request-payload.bin" now-long)]
        (log/info "write payload to:" fname)
        (with-open [w (io/output-stream fname)]
          (.write w payload)))
      )
    )
  (defn send-response [msg]
    (log/debug "sending a response")
    (lb/publish ch exchange "response" msg (response-properties metadata))
    )
  (-> (parse-and-validate metadata payload)
      (x/indent-str)
      (send-response)
      )
  (lb/ack ch (:delivery-tag metadata))
  (log/info "message ack")
  )




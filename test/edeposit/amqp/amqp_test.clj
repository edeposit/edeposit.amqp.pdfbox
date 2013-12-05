(ns edeposit.amqp.amqp-test
  (:require [edeposit.amqp.pdfbox.amqp :as amqp]
            [edeposit.amqp.pdfbox.core :as core]
            [clojure.java.io :as io]
            [clojure.zip :as zip]
            [clojure.data.zip.xml :as xml]
            [clojure.data.xml :as x]
            [clojure.xml]
            )
  (:use clojure.test)
  (:import [java.util Date]
           [java.util UUID]
           )
  )

;; (def fname "resources/test-pdf.pdf")
;; (def file (io/file fname))
;; (def result (core/xmlValidationOutput file))
;; (def xmldata (zip/xml-zip result))
;; (xml/text xmldata)
;; (println (x/indent-str xmldata))
;; (def aa (zip/next xmldata))

;; (xml/text aa)

;; (first (xml/xml-> xmldata :validation :validationErrors))
;; (map xml/text  (xml/xml-> xmldata :validation))
;; (map xml/text (xml/xml-> xmldata :characterization :creator))
;; (map xml/text (xml/xml-> xmldata :characterization))

;; (def file (io/file "/tmp/aa.xml"))
;; (def xmldata (clojure.xml/parse file))
;; (def root (zip/xml-zip xmldata))
;; (defn next [loc num]
;;   (apply comp (repeat 5 zip/next))
;;   )


;; (def next (apply comp (repeat 36 zip/next)))
;; (-> root next first :tag)
;; (def valerrors (-> root next first))

;; (clojure.xml/emit-element valerrors)

;; (def next0 (apply comp (repeat 37 zip/next)))

;; (defn error-as-string [elem]
;;   (clojure.string/join " : " [(-> elem :attrs :errorCode) (-> :content elem first)])
;;   )

;; (def error (-> root next0 first))
;; (error-as-string error)

;; (error-as-string (-> root next0 first))
;; (-> root next0 first :attrs :errorCode)

;; (if (= 1 1) "ano" "ne")

;; (loop [loc root
;;        errors []
;;        ]
;;   (let [elem (first loc)]
;;     (if (zip/end? loc)
;;       (if (= (:tag elem) :error)
;;         (conj errors "ahoj")
;;         errors
;;         )
;;       (recur (zip/next loc) errors)
;;       )
;;     )
;;   )
 
(deftest data-test-01
  (let [fname "resources/test-pdf-from-ocr.pdf"
        file (io/file fname)
        result (core/xmlValidationOutput file)
        xmldata (zip/xml-zip result)
        data (amqp/makeDataWithUrl file)
        ]
    (testing (format "creating of amqp message for file: %s" fname)
      (is (not (clojure.string/blank? (:uuid data))))
      (is (= (:url data) (.getAbsolutePath file)))
      )
    (testing (format "processing of amqp message for file: %s" fname)
      (let [result (amqp/processDataWithUrl data)]
                                        ;(println (:errors result))
                                        ;(println (:metadata result))
                                        ;(println (x/indent-str xmldata))
        )
      )
    )
  )

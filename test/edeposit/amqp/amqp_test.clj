(ns edeposit.amqp.amqp-test
  (:require [edeposit.amqp.pdfbox.amqp :as amqp]
            [edeposit.amqp.pdfbox.core :as core]
            [clojure.java.io :as io]
            [clojure.zip :as zip]
            [clojure.data.zip.xml :as xml]
            [clojure.data.xml :as x]
            )
  (:use clojure.test)
  (:import [java.util Date]
           [java.util UUID]
           )
  )

(deftest xml-test-01
  (let [fname "resources/test-pdfa-1a.pdf"
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
        )
      )
    )
  )

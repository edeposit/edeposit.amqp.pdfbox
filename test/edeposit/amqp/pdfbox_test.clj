(ns edeposit.amqp.pdfbox-test
  (:require [edeposit.amqp.pdfbox.core :as core]
            [clojure.java.io :as io]
            [clojure.zip :as zip]
            [clojure.data.zip.xml :as xml]
            [clojure.data.xml :as x]
            )
  (:use clojure.test)
  (:import [java.util Date])
  )

(deftest xml-test-01
  (let [ file (io/file "resources/test-pdfa-1a.pdf")
         data (core/xmlValidationOutput file)
         xmldata (zip/xml-zip data)
        ]
    (testing "xml tags tests for file test-pdfa-1a.pdf"
      (is (= (first (xml/xml-> xmldata :identification :fileSize xml/text))
             (-> file .length .toString)))
      (is (= (first (xml/xml-> xmldata :identification :filePath xml/text))
             (.getAbsolutePath file)))
      (is (= (first (xml/xml-> xmldata :identification :lastModified xml/text))
             (format "%s" (new Date (.lastModified file)))))
      (is (= (first (xml/xml-> xmldata :characterization :isEncrypted xml/text)) "false"))
      (is (= (first (xml/xml-> xmldata :characterization :numOfPages xml/text)) "1"))
      (is (= (first (xml/xml-> xmldata :validation :isValidPDFA xml/text)) "true"))
      ;(println (x/indent-str data))
      )
    )
  )

(deftest xml-test-02
  (let [ file (io/file "resources/test-pdf.pdf")
         data (core/xmlValidationOutput file)
         xmldata (zip/xml-zip data)
        ]
    (testing "xml tags tests for file test-pdf.pdf"
      (is (= (first (xml/xml-> xmldata :identification :fileSize xml/text))
             (-> file .length .toString)))
      (is (= (first (xml/xml-> xmldata :identification :filePath xml/text))
             (.getAbsolutePath file)))
      (is (= (first (xml/xml-> xmldata :identification :lastModified xml/text))
             (format "%s" (new Date (.lastModified file)))))
      (is (= (first (xml/xml-> xmldata :characterization :isEncrypted xml/text)) "false"))
      (is (= (first (xml/xml-> xmldata :characterization :numOfPages xml/text)) "1"))
      (is (= (first (xml/xml-> xmldata :validation :isValidPDFA xml/text)) "false"))

      )
    )
  )

(deftest xml-test-03
  (let [ file (io/file "resources/test-pdf-from-ocr.pdf")
         data (core/xmlValidationOutput file)
         xmldata (zip/xml-zip data)
        ]
    (testing "xml tags tests for file test-pdf-from-ocr.pdf"
      (is (= (first (xml/xml-> xmldata :identification :fileSize xml/text))
             (-> file .length .toString)))
      (is (= (first (xml/xml-> xmldata :identification :filePath xml/text))
             (.getAbsolutePath file)))
      (is (= (first (xml/xml-> xmldata :identification :lastModified xml/text))
             (format "%s" (new Date (.lastModified file)))))
      (is (= (first (xml/xml-> xmldata :characterization :isEncrypted xml/text)) "false"))
      (is (= (first (xml/xml-> xmldata :characterization :numOfPages xml/text)) "1"))
      (is (= (first (xml/xml-> xmldata :validation :isValidPDFA xml/text)) "false"))

      )
    )
  )

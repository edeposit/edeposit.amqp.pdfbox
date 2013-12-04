(ns edeposit.amqp.pdfbox-test
  (:require [edeposit.amqp.pdfbox.core :as core]
            [clojure.java.io :as io]
            [clojure.zip :as zip]
            [clojure.data.zip.xml :as xml]
            [clojure.data.xml :as x]
            )
  (:use clojure.test)
  (:import [java.util Date]
           [org.apache.pdfbox Version]
           )
  )

(defn xml-test-is-valid [fname file xmldata]
  (testing (format "test of validation section pdfa: %s" fname)
    (is (= (first (xml/xml-> xmldata :validation :isValidPDFA xml/text)) "true"))
    )
  )

(defn xml-test-is-not-valid [fname file xmldata]
  (testing (format "test of validation section pdfa: %s" fname)
    (is (= (first (xml/xml-> xmldata :validation :isValidPDFA xml/text)) "false"))
    )
  )

(defn xml-test [fname file xmldata]
  (testing (format "xml tags tests for: %s" fname)
    (is (= (first (xml/xml-> xmldata :extractor :version xml/text)) (Version/getVersion)))
    (is (= (first (xml/xml-> xmldata :identification :fileSize xml/text)) (-> file .length .toString)))
    (is (= (first (xml/xml-> xmldata :identification :filePath xml/text))  (.getAbsolutePath file)))
    (is (= (first (xml/xml-> xmldata :identification :lastModified xml/text))
           (format "%s" (new Date (.lastModified file)))))
    (is (= (first (xml/xml-> xmldata :characterization :isEncrypted xml/text)) "false"))
    (is (= (first (xml/xml-> xmldata :characterization :numOfPages xml/text)) "1"))
    )

  )

(deftest xml-test-01
  (let [fname "resources/test-pdfa-1a.pdf"
        file (io/file fname)
        data (core/xmlValidationOutput file)
        xmldata (zip/xml-zip data)
        ]
    (xml-test fname file xmldata)
    (xml-test-is-valid fname file xmldata)
    (testing (format "testing xml, section characterization %s" fname)
      (is (= (first (xml/xml-> xmldata :characterization :author xml/text)) "Vychodil Bedřich"))
      (is (= (first (xml/xml-> xmldata :characterization :title xml/text)) ""))
      (is (= (first (xml/xml-> xmldata :characterization :subject  xml/text)) ""))
      (is (= (first (xml/xml-> xmldata :characterization :keywords  xml/text)) ""))
      (is (= (first (xml/xml-> xmldata :characterization :creator  xml/text)) "Microsoft® Word 2010"))
      (is (= (first (xml/xml-> xmldata :characterization :producer  xml/text)) "Microsoft® Word 2010"))
      )
    (testing (format "testing xml, section identification %s" fname)
      ;(is (= (first (xml/xml-> xmldata :identification :created xml/text)) "2013-10-23T12:36:47.000+02:00"))
      (is (= (first (xml/xml-> xmldata :identification :trapped xml/text)) ""))
      )
    )
  )
  
(deftest xml-test-02
  (let [fname "resources/test-pdf.pdf"
        file (io/file fname)
        data (core/xmlValidationOutput file)
        xmldata (zip/xml-zip data)
        ]
    (xml-test fname file xmldata)
    (xml-test-is-not-valid fname file xmldata)
    (testing (format "testing xml, section characterization %s" fname)
      (is (= (first (xml/xml-> xmldata :characterization :author xml/text)) "Vychodil Bedřich"))
      (is (= (first (xml/xml-> xmldata :characterization :title xml/text)) ""))
      (is (= (first (xml/xml-> xmldata :characterization :subject  xml/text)) ""))
      (is (= (first (xml/xml-> xmldata :characterization :keywords  xml/text)) ""))
      (is (= (first (xml/xml-> xmldata :characterization :creator  xml/text)) "Microsoft® Word 2010"))
      (is (= (first (xml/xml-> xmldata :characterization :producer  xml/text)) "Microsoft® Word 2010"))
      )
    (testing (format "testing xml, section identification %s" fname)
      ;(is (= (first (xml/xml-> xmldata :identification :created xml/text)) "2013-10-23T11:48:37.000+02:00"))
      (is (= (first (xml/xml-> xmldata :identification :trapped xml/text)) ""))
      )
    )
  )

(deftest xml-test-03
  (let [fname "resources/test-pdf-from-ocr.pdf"
        file (io/file fname)
        data (core/xmlValidationOutput file)
        xmldata (zip/xml-zip data)
        ]
    (xml-test fname file xmldata)
    (xml-test-is-not-valid fname file xmldata)
    (testing (format "testing xml, section characterization %s" fname)
      (is (= (first (xml/xml-> xmldata :characterization :author xml/text)) ""))
      (is (= (first (xml/xml-> xmldata :characterization :title  xml/text)) ""))
      (is (= (first (xml/xml-> xmldata :characterization :subject  xml/text)) ""))
      (is (= (first (xml/xml-> xmldata :characterization :keywords  xml/text)) ""))
      (is (= (first (xml/xml-> xmldata :characterization :creator  xml/text)) "Adobe Acrobat 9.5.4"))
      (is (= (first (xml/xml-> xmldata :characterization :producer  xml/text)) "Adobe Acrobat 9.54 Paper Capture Plug-in"))
      )
    (testing (format "testing xml, section identification %s" fname)
      ;(is (= (first (xml/xml-> xmldata :identification :created xml/text)) "2013-10-23T11:28:08.000+02:00"))
      (is (= (first (xml/xml-> xmldata :identification :trapped xml/text)) ""))
      )
    )
  )



(ns edeposit.amqp.pdfbox-test
  (:require [edeposit.amqp.pdfbox.core :as core]
            [clojure.java.io :as io]
            [clojure.zip :as zip]
            [clojure.data.zip.xml :as xml]
            [clojure.data.xml :as x]
            [edeposit.amqp.pdfbox.handlers :as handlers]
            )
  (:use clojure.test)
  (:import [java.util Date]
           [org.apache.pdfbox Version]
           [org.apache.commons.lang3 StringEscapeUtils]
           )
  )

(defn xml-test-is-valid [fname file xmldata]
  (testing (format "test of validation section pdfa: %s" fname)
    (is (= (first (xml/xml-> xmldata :validation :isValidPDF xml/text)) "true"))
    (is (= (first (xml/xml-> xmldata :validation :isValidPDFA xml/text)) "true"))
    )
  )

(defn xml-test-is-not-valid [fname file xmldata]
  (testing (format "test of validation section pdfa: %s" fname)
    (is (= (first (xml/xml-> xmldata :validation :isValidPDF xml/text)) "true"))
    (is (= (first (xml/xml-> xmldata :validation :isValidPDFA xml/text)) "false"))
    )
  )

(defn xml-test [fname file xmldata & {:keys [num-of-pages xmp-fname
                                             ]
                                      :or [:num-of-pages "1" :xmp-fname ""]
                                      }]
  (testing (format "xml tags tests for: %s" fname)
    (is (= (first (xml/xml-> xmldata :extractor :version xml/text)) (Version/getVersion)))
    (is (= (first (xml/xml-> xmldata :extractor :name xml/text)) "PDFBox Apache.org"))
    (is (= (first (xml/xml-> xmldata :identification :fileSize xml/text)) (-> file .length .toString)))
    (is (= (first (xml/xml-> xmldata :identification :filePath xml/text))  (.getAbsolutePath file)))
    (is (= (first (xml/xml-> xmldata :identification :lastModified xml/text))
           (format "%s" (new Date (.lastModified file)))))
    (is (= (first (xml/xml-> xmldata :characterization :isEncrypted xml/text)) "false"))
    (is (= (first (xml/xml-> xmldata :characterization :numOfPages xml/text)) num-of-pages))
    )
  )

(deftest xml-test-01
  (let [fname "resources/test-pdfa-1a.pdf"
        file (io/file fname)
        data (core/validate file)
        xmldata (zip/xml-zip data)
        ]
    (xml-test fname file xmldata :num-of-pages "1")
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
    (testing (format "xmp tests for: %s" fname)
      ;(is (= (first (xml/xml-> xmldata :xmp xml/text)) (slurp "/tmp/xmp.xml")))
      )
    ;(println (x/indent-str xmldata))
    )
  )
  
(deftest xml-test-02
  (let [fname "resources/test-pdf.pdf"
        file (io/file fname)
        data (core/validate file)
        xmldata (zip/xml-zip data)
        ]
    (xml-test fname file xmldata :num-of-pages "1")
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
        data (core/validate file)
        xmldata (zip/xml-zip data)
        ]
    (xml-test fname file xmldata :num-of-pages "1")
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
    ;;(println (x/indent-str xmldata))
    )
  )

;; (deftest xml-test-04
;;   (let [fname "resources/xmpspecification.pdf"
;;         file (io/file fname)
;;         data (core/validate file)
;;         xmldata (zip/xml-zip data)
;;         ]
;;     (xml-test fname file xmldata :num-of-pages "94")
;;     (xml-test-is-not-valid fname file xmldata)
;;     (testing (format "testing xml, section characterization %s" fname)
;;       (is (= (first (xml/xml-> xmldata :characterization :author xml/text)) "Adobe Developer Technologies"))
;;       (is (= (first (xml/xml-> xmldata :characterization :title  xml/text)) "XMP - Extensible Metadata Platform"))
;;       (is (= (first (xml/xml-> xmldata :characterization :subject  xml/text)) "XMP Metadata"))
;;       (is (= (first (xml/xml-> xmldata :characterization :keywords  xml/text)) "XMP metadata schema XML RDF"))
;;       (is (= (first (xml/xml-> xmldata :characterization :creator  xml/text)) "FrameMaker 7.0"))
;;       (is (= (first (xml/xml-> xmldata :characterization :producer  xml/text)) "Acrobat Distiller 5.0.5 for Macintosh"))
;;       )
;;     (testing (format "testing xml, section identification %s" fname)
;;       ;(is (= (first (xml/xml-> xmldata :identification :created xml/text)) "2013-10-23T11:28:08.000+02:00"))
;;       (is (= (first (xml/xml-> xmldata :identification :trapped xml/text)) ""))
;;       )
;;     )
;;   )

(deftest xml-test-05
  (let [fname "resources/xmp_metadata_added.pdf"
        file (io/file fname)
        data (core/validate file)
        xmldata (zip/xml-zip data)
        ]
    (xml-test fname file xmldata :num-of-pages "1")
    (xml-test-is-not-valid fname file xmldata)
    (testing (format "testing xml, section characterization %s" fname)
      (is (= (first (xml/xml-> xmldata :characterization :author xml/text)) "Bruno Lowagie"))
      (is (= (first (xml/xml-> xmldata :characterization :title  xml/text)) "Hello World example"))
      (is (= (first (xml/xml-> xmldata :characterization :subject  xml/text)) "This example shows how to add metadata"))
      (is (= (first (xml/xml-> xmldata :characterization :keywords  xml/text)) "Metadata, iText, PDF"))
      (is (= (first (xml/xml-> xmldata :characterization :creator  xml/text)) "My program using iText"))
      (is (= (first (xml/xml-> xmldata :characterization :producer  xml/text)) "iText® 5.4.5 ©2000-2013 1T3XT BVBA (AGPL-version)"))
      )
    (testing (format "testing xml, section identification %s" fname)
      ;(is (= (first (xml/xml-> xmldata :identification :created xml/text)) "2013-10-23T11:28:08.000+02:00"))
      (is (= (first (xml/xml-> xmldata :identification :trapped xml/text)) ""))
      )
    ;; (println "ahoj")
    ;; (println (x/indent-str xmldata))
    )
  )

(deftest xml-test-05
  (let [fname "resources/corrupted.pdf"
        file (io/file fname)
        data (core/validate file)
        xmldata (zip/xml-zip data)
        ]

    (is (= (first (xml/xml-> xmldata :extractor :version xml/text)) (Version/getVersion)))
    (is (= (first (xml/xml-> xmldata :extractor :name xml/text)) "PDFBox Apache.org"))
    (is (= (first (xml/xml-> xmldata :identification :fileSize xml/text)) (-> file .length .toString)))
    (is (= (first (xml/xml-> xmldata :identification :filePath xml/text))  (.getAbsolutePath file)))
    (is (= (first (xml/xml-> xmldata :identification :lastModified xml/text))
           (format "%s" (new Date (.lastModified file)))))
    (is (= (first (xml/xml-> xmldata :characterization :isEncrypted xml/text)) ""))
    (is (= (first (xml/xml-> xmldata :characterization :numOfPages xml/text)) ""))

    (testing (format "test of validation section pdfa: %s" fname)
      (is (= (first (xml/xml-> xmldata :validation :isValidPDF xml/text)) "false"))
      (is (= (first (xml/xml-> xmldata :validation :isValidPDFA xml/text)) "false"))
      )
    (testing (format "testing xml, section characterization %s" fname)
      (is (= (first (xml/xml-> xmldata :characterization :author xml/text)) ""))
      (is (= (first (xml/xml-> xmldata :characterization :title  xml/text)) ""))
      (is (= (first (xml/xml-> xmldata :characterization :subject  xml/text)) ""))
      (is (= (first (xml/xml-> xmldata :characterization :keywords  xml/text)) ""))
      (is (= (first (xml/xml-> xmldata :characterization :creator  xml/text)) ""))
      (is (= (first (xml/xml-> xmldata :characterization :producer  xml/text)) ""))
      )
    (testing (format "testing xml, section identification %s" fname)
      (is (= (first (xml/xml-> xmldata :identification :created xml/text)) ""))
      (is (= (first (xml/xml-> xmldata :identification :trapped xml/text)) ""))
      )
    )
  )


(deftest xml-test-06
  (let [fname "resources/corrupted.pdf"
        file (io/file fname)
        data (core/validate file)
        xmldata (zip/xml-zip data)
        ]

    (is (= (first (xml/xml-> xmldata :extractor :version xml/text)) (Version/getVersion)))
    (is (= (first (xml/xml-> xmldata :extractor :name xml/text)) "PDFBox Apache.org"))
    (is (= (first (xml/xml-> xmldata :identification :fileSize xml/text)) (-> file .length .toString)))
    (is (= (first (xml/xml-> xmldata :identification :filePath xml/text))  (.getAbsolutePath file)))
    (is (= (first (xml/xml-> xmldata :identification :lastModified xml/text))
           (format "%s" (new Date (.lastModified file)))))
    (is (= (first (xml/xml-> xmldata :characterization :isEncrypted xml/text)) ""))
    (is (= (first (xml/xml-> xmldata :characterization :numOfPages xml/text)) ""))

    (testing (format "testing xml unicode letters, section identification %s" fname)
      (testing (format "testing xml, section validation %s" fname) 
        (is (= (xml/xml1-> xmldata :validation :isValidPDF xml/text) "false"))
        (is (= (xml/xml1-> xmldata :validation :isValidPDFA xml/text) "false"))
        (is (.contains (xml/xml1-> xmldata :validation :validationErrors :error xml/text) "Error"))
        (is (.contains (xml/xml1-> xmldata :validation :validationErrors :error xml/text) "Expected a long type"))
        (is (= (xml/xml1-> xmldata :validation :validationErrors :error (xml/attr :errorCode)) "exception"))
        )
      ;; (let [out (x/indent-str xmldata)]
      ;;   (println out)
      ;;   )
      )
    )
  )

(deftest handlers-test-01
  (let [fname "resources/test-pdf.pdf" 
        file (io/file fname)
        metadata (read-string (slurp "resources/request-metadata.clj"))
        payload (.getBytes (slurp "resources/request-payload.bin"))
        result (handlers/parse-and-validate metadata payload)
        xmldata (zip/xml-zip result)
        ]

    (testing (format "testing amqp handler")
      (testing (format "test of validation section pdfa: %s" fname)
        (is (= (first (xml/xml-> xmldata :validation :isValidPDF xml/text)) "true"))
        (is (= (first (xml/xml-> xmldata :validation :isValidPDFA xml/text)) "false"))
        )

      (testing (format "testing xml, section characterization %s" fname)
        (is (= (first (xml/xml-> xmldata :characterization :author xml/text)) "Vychodil Bedřich"))
        (is (= (first (xml/xml-> xmldata :characterization :title xml/text)) ""))
        (is (= (first (xml/xml-> xmldata :characterization :subject  xml/text)) ""))
        (is (= (first (xml/xml-> xmldata :characterization :keywords  xml/text)) ""))
        (is (= (first (xml/xml-> xmldata :characterization :creator  xml/text)) "Microsoft® Word 2010"))
        (is (= (first (xml/xml-> xmldata :characterization :producer  xml/text)) "Microsoft® Word 2010"))
      )
      (testing (format "testing xml, section identification %s" fname)
        (is (= (first (xml/xml-> xmldata :identification :trapped xml/text)) ""))
        )
      )
    )
  )

(deftest handlers-fix-01
  (let [fname "resources/fix01-file.pdf" 
        file (io/file fname)
        metadata (read-string (slurp "resources/fix01-request-metadata.clj"))
        payload (.getBytes (slurp "resources/fix01-request-payload.bin"))
        result (handlers/parse-and-validate metadata payload)
        xmldata (zip/xml-zip result)
        ]
    
    (testing (format "testing amqp handler")
      (testing (format "test of validation section pdfa: %s" fname)
        (is (= (first (xml/xml-> xmldata :validation :isValidPDF xml/text)) "true"))
        (is (= (first (xml/xml-> xmldata :validation :isValidPDFA xml/text)) "false"))
        )

      (testing (format "testing xml, section characterization %s" fname)
        (is (= (first (xml/xml-> xmldata :characterization :author xml/text)) ""))
        (is (= (first (xml/xml-> xmldata :characterization :title xml/text)) ""))
        (is (= (first (xml/xml-> xmldata :characterization :subject  xml/text)) ""))
        (is (= (first (xml/xml-> xmldata :characterization :keywords  xml/text)) ""))
        (is (= (first (xml/xml-> xmldata :characterization :creator  xml/text)) "Canon iR-ADV 6055 PDF"))
        (is (= (first (xml/xml-> xmldata :characterization :producer  xml/text)) (StringEscapeUtils/escapeXml10 "Adobe PDF Scan Library 1.0e for Canon imageRUNNER ")))
      )
      (testing (format "testing xml, section identification %s" fname)
        (is (= (first (xml/xml-> xmldata :identification :trapped xml/text)) ""))
        )
      (testing (format "testing xml serialization of an result %s" fname)
        (is (.startsWith (x/indent-str result) "<?xml version=\"1.0\""))
        )
      )
    (is (= (xml/xml1-> xmldata :validation :validationErrors :error (xml/attr :errorCode)) nil))
    )
  )

(deftest response-test-01
  (let [fname "resources/fix01-file.pdf" 
        file (io/file fname)
        metadata (read-string (slurp "resources/fix01-request-metadata.clj"))
        payload (.getBytes (slurp "resources/fix01-request-payload.bin"))
        result (handlers/parse-and-validate metadata payload)
        ]
    
    (testing (format "response copies UUID at headers")
      (let [response-properties (handlers/response-properties metadata)]
        (is (= (-> response-properties :headers (get "UUID")) (-> metadata :headers (get "UUID"))))
        )
      )
    )
  )

(deftest handlers-fix-02
  (let [fname "resources/fix02-file.pdf"
        metadata (read-string (slurp "resources/fix02-request-metadata.clj"))
        payload (.getBytes (slurp "resources/fix02-request-payload.bin"))
        ]
    
    (testing (format "testing amqp handler")
      (let [result (handlers/parse-and-validate metadata payload)
            xmldata (zip/xml-zip result)]

        ;;(-> xmldata  x/indent-str println)

        (testing (format "test of validation section pdfa: %s" fname)
          (is (= (first (xml/xml-> xmldata :validation :isValidPDF xml/text)) "false"))
          (is (= (first (xml/xml-> xmldata :validation :isValidPDFA xml/text)) "false"))
          )
        (testing (format "testing xml, section characterization %s" fname)
          (is (= (first (xml/xml-> xmldata :characterization :author xml/text)) ""))
          (is (= (first (xml/xml-> xmldata :characterization :title xml/text)) ""))
          (is (= (first (xml/xml-> xmldata :characterization :subject  xml/text)) ""))
          (is (= (first (xml/xml-> xmldata :characterization :keywords  xml/text)) ""))
          (is (= (first (xml/xml-> xmldata :characterization :creator  xml/text)) ""))
          (is (= (first (xml/xml-> xmldata :characterization :producer  xml/text)) ""))
          )
        (testing (format "testing xml, section identification %s" fname)
          (is (= (first (xml/xml-> xmldata :identification :trapped xml/text)) ""))
          )    
        (testing (format "testing xml serialization of an result %s" fname)
          (is (.startsWith (x/indent-str result) "<?xml version=\"1.0\""))
          )    
        (is (= (xml/xml1-> xmldata :validation :validationErrors :error (xml/attr :errorCode)) "exception"))
        )
      )
    )
  )

(deftest handlers-fix-03
  (let [metadata (read-string (slurp "resources/fix03-request-metadata.clj"))
        payload (.getBytes (slurp "resources/fix03-request-payload.bin"))
        ]
    
    (testing (format "testing amqp handler - fix03")
      (let [result (handlers/parse-and-validate metadata payload)
            xmldata (zip/xml-zip result)]

        ;;(-> xmldata  x/indent-str println)

        (testing "test of validation section pdfa"
          (is (= (first (xml/xml-> xmldata :validation :isValidPDF xml/text)) "false"))
          (is (= (first (xml/xml-> xmldata :validation :isValidPDFA xml/text)) "false"))
          )
        (testing "testing xml, section characterization"
          (is (= (first (xml/xml-> xmldata :characterization :author xml/text)) ""))
          (is (= (first (xml/xml-> xmldata :characterization :title xml/text)) ""))
          (is (= (first (xml/xml-> xmldata :characterization :subject  xml/text)) ""))
          (is (= (first (xml/xml-> xmldata :characterization :keywords  xml/text)) ""))
          (is (= (first (xml/xml-> xmldata :characterization :creator  xml/text)) ""))
          (is (= (first (xml/xml-> xmldata :characterization :producer  xml/text)) ""))
          )
        (testing "testing xml, section identification"
          (is (= (first (xml/xml-> xmldata :identification :trapped xml/text)) ""))
          )    
        (testing "testing xml serialization of an result"
          (is (.startsWith (x/indent-str result) "<?xml version=\"1.0\""))
          )    
        (is (= (xml/xml1-> xmldata :validation :validationErrors :error (xml/attr :errorCode)) "exception"))
        )
      )
    )
  )

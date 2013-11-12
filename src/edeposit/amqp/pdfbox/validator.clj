(ns edeposit.amqp.pdfbox.validator
  (:require [clojure.java.io :as io]
            [clojure.pprint :as pp]
            [clojure.data.xml :as xml]
            [edeposit.amqp.pdfbox.utils :as utils]
            [clojure.tools.cli :as cli]
            )
  (:import [org.apache.pdfbox.pdmodel PDDocument PDPage PDDocumentInformation]
           [org.apache.pdfbox.pdmodel.edit PDPageContentStream]
           [org.apache.pdfbox.pdmodel.font PDType1Font]
           [org.apache.pdfbox.preflight.parser PreflightParser]
           [org.apache.pdfbox.preflight PreflightDocument]
           [java.util Date]
           )
  )
 
(def test-file "resources/1002186430_000015 Born digital - OCR z TIFFu.pdf")
(def pddocument (PDDocument/load test-file))
(utils/list-methods pddocument)
(def docinfo (.getDocumentInformation pddocument))
(.getTitle docinfo)
(defn getMetadata [ pddocument ]
  )

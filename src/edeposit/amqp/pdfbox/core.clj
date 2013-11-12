(ns edeposit.amqp.pdfbox.core
  (:require [clojure.java.io :as io]
            [clojure.pprint :as pp]
            [clojure.data.xml :as xml]
            [edeposit.amqp.pdfbox.utils :as utils]
            [clojure.tools.cli :as cli]
            )
  (:import [org.apache.pdfbox.pdmodel PDDocument PDPage]
           [org.apache.pdfbox.pdmodel.edit PDPageContentStream]
           [org.apache.pdfbox.pdmodel.font PDType1Font]
           [org.apache.pdfbox.preflight.parser PreflightParser]
           [org.apache.pdfbox.preflight PreflightDocument]
           [java.util Date]
           )
  (:gen-class :main true)
  )

(defn xmlValidationOutput [test-file]
  (let [pddocument (PDDocument/load test-file)
        parser (new PreflightParser test-file)
        ]
    (do
      (.parse parser)
      (def preflightDocument (.getPreflightDocument parser))
      (.validate preflightDocument)
      (def result (.getResult preflightDocument))
      (.close preflightDocument)

      (xml/element :result {}
                   (xml/element :identification {}
                                (xml/element :fileSize {} (format "%s" (.length test-file)))
                                (xml/element :filePath {} (format "%s" (.getAbsolutePath test-file)))
                                (xml/element :lastModified {} (format "%s" (new Date (.lastModified test-file))))
                                )
                   (xml/element :characterization {}
                                (xml/element :isEncrypted {} (format "%s" (.isEncrypted pddocument)))
                                (xml/element :numOfPages {} (format "%s" (.getNumberOfPages pddocument)))     
                                )
                   
                   (xml/element :validation {}
                                (xml/element :isValidPDFA1 {} (format "%s" (.isValid result)))
                                (xml/element :validationErrors {}
                                             (map #(xml/element :error { :errorCode (format "%s" (.getErrorCode %))}
                                                                (format "%s" (.getDetails %)))
                                                  (.getErrorsList result)))
                                )
                   )
      )
    )
  )

(defn -main [& args]
  (let [ [options args banner] (cli/cli args
                                        [ "-f" "--file" :default "resources/1002186430_000015 Born digital - OCR z TIFFu.pdf"]
                                        [ "--amqp" :default false :flag true]
                                        [ "-h" "--help" :default false :flag true]
                                        )
         ]
    (when (:help options)
      (println banner)
      (System/exit 0)
      )
    (println (xml/indent-str (xmlValidationOutput (io/file (:file options)))))
    )
  )

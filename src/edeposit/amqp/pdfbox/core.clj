(ns edeposit.amqp.pdfbox.core
  (:require [clojure.java.io :as io]
            [clojure.pprint :as pp]
            [clojure.data.xml :as xml]
            [edeposit.amqp.pdfbox.utils :as utils]
            [clojure.tools.cli :as cli]
            [clj-time.format :as format]
            )
  (:import [org.apache.pdfbox.pdmodel PDDocument PDPage]
           [org.apache.pdfbox.pdmodel.edit PDPageContentStream]
           [org.apache.pdfbox.pdmodel.font PDType1Font]
           [org.apache.pdfbox.preflight.parser PreflightParser]
           [org.apache.pdfbox.preflight PreflightDocument]
           [java.util Date]
           [org.apache.pdfbox Version]
           [java.text SimpleDateFormat]
           )
  (:gen-class :main true)
  )

(def test-file (io/file "resources/test-pdf.pdf"))
(def pddocument (PDDocument/load test-file))
(def info (.getDocumentInformation pddocument))
(def formatter (format/formatters :date-time :basic-time))
(format/show-formatters)
(def created (.getCreationDate info))

;; (format/parse formatter created)
;; (format/show-formatters)

;; (def dict (.getDictionary info))
;; (.keyList dict)
;; (.getValues dict)
;; (.entrySet dict)
;; (def cat (.getDocumentCatalog pddocument))
;; (def meta (.getMetadata cat))

(defn format-date [value]
  (def formatter (format/formatters :basic-date-time))
  (if (or (nil? value) (empty value))
    ""
    (if (integer? value)
      (format "%s" (new Date value))
      (if (string? value)
        value
        (format "%s" value)
        )
      )
    )
  )

;(format-date (.lastModified test-file))

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
      (def info (.getDocumentInformation pddocument))
      (def fmt (new SimpleDateFormat "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))

      (xml/element :result {}
                   (xml/element :extractor {}
                                (xml/element :version {} (Version/getVersion))
                                )
                   (xml/element :identification {}
                                (xml/element :fileSize {} (format "%s" (.length test-file)))
                                (xml/element :filePath {} (format "%s" (.getAbsolutePath test-file)))
                                (xml/element :lastModified {} (format-date (.lastModified test-file)))
                                (xml/element :created {} (.format fmt (.getTime (.getCreationDate info))))
                                (xml/element :trapped {} (.getTrapped info))
                                )
                   (xml/element :characterization {}
                                (xml/element :isEncrypted {} (format "%s" (.isEncrypted pddocument)))
                                (xml/element :numOfPages {} (format "%s" (.getNumberOfPages pddocument)))
                                (xml/element :author {} (.getAuthor info))
                                (xml/element :title {} (.getTitle info))
                                (xml/element :subject {} (.getSubject info))
                                (xml/element :keywords {} (.getKeywords info))
                                (xml/element :creator {} (.getCreator info))
                                (xml/element :producer {} (.getProducer info))
                                )
                   
                   (xml/element :validation {}
                                (xml/element :isValidPDFA {} (format "%s" (.isValid result)))
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

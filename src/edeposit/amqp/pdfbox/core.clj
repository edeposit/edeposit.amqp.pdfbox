(ns edeposit.amqp.pdfbox.core
  (:require [clojure.java.io :as io]
            [clojure.pprint :as pp]
            [clojure.data.xml :as xml]
            [edeposit.amqp.pdfbox.utils :as utils]
            [clojure.tools.cli :as cli]
            [clj-time.format :as format]
            [clojure.reflect :as cr]
            [clojure.pprint :as pp]
            )
  (:import [org.apache.pdfbox.pdmodel PDDocument PDPage]
           [org.apache.pdfbox.pdmodel.edit PDPageContentStream]
           [org.apache.pdfbox.pdmodel.font PDType1Font]
           [org.apache.pdfbox.preflight.parser PreflightParser]
           [org.apache.pdfbox.preflight PreflightDocument]
           [java.util Date]
           [org.apache.pdfbox Version]
           [java.text SimpleDateFormat]
           [java.text Normalizer]
           [java.text.Normalizer$Form]
           [org.apache.commons.lang3 StringEscapeUtils]
           )
  )

(defn format-date [value]
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

(defn normalize-string [string]
  (-> string
      (Normalizer/normalize java.text.Normalizer$Form/NFD)
      (.replaceAll "\\P{InBasic_Latin}" ".")
      (.replaceAll "\\p{C}" ".")
      )
  )

(defn validate [test-file]
  (try
    (def pddocument (PDDocument/load test-file))
    (def parser (new PreflightParser test-file)  )
    (.parse parser)
    (def preflightDocument (.getPreflightDocument parser))
    (.validate preflightDocument)
    (def result (.getResult preflightDocument))
    (.close preflightDocument)
    (def info (.getDocumentInformation pddocument))
    (def catalog (.getDocumentCatalog pddocument))
    (def metadata (.getMetadata catalog))
    (def fmt (new SimpleDateFormat "yyyy-MM-dd'T'HH:mm:ss.SSSZ"))

    (xml/element :result {}
                 (xml/element :extractor {}
                              (xml/element :name {} "PDFBox Apache.org")
                              (xml/element :version {} (Version/getVersion))
                              )
                 (xml/element :identification {}
                              (xml/element :fileSize {} (format "%s" (.length test-file)))
                              (xml/element :filePath {} (format "%s" (.getAbsolutePath test-file)))
                              (xml/element :lastModified {} (format-date (.lastModified test-file)))
                              (xml/element :created {} (.format fmt (.getTime (.getCreationDate info))) )
                              (xml/element :trapped {} (.getTrapped info))
                              )
                 (xml/element :characterization {}
                              (xml/element :isEncrypted {} (format "%s" (.isEncrypted pddocument)))
                              (xml/element :numOfPages {} (format "%s" (.getNumberOfPages pddocument)))
                              (xml/element :author {} (StringEscapeUtils/escapeXml10 (.getAuthor info)))
                              (xml/element :title {}  (StringEscapeUtils/escapeXml10 (.getTitle info)))
                              (xml/element :subject {} (StringEscapeUtils/escapeXml10 (.getSubject info)))
                              (xml/element :keywords {} (.getKeywords info))
                              (xml/element :creator {} (StringEscapeUtils/escapeXml10 (.getCreator info)))
                              (xml/element :producer {} (StringEscapeUtils/escapeXml10 (.getProducer info)))
                              )
                 (xml/element :validation {}
                              (xml/element :isValidPDF {} (format "%s" true))
                              (xml/element :isValidPDFA {} (format "%s" (.isValid result)))
                              (xml/element :validationErrors {}
                                           (map #(xml/element :error { :errorCode (normalize-string (format "%s" (.getErrorCode %)))}
                                                              (normalize-string (format "%s" (.getDetails %))))
                                                (.getErrorsList result)))
                              )
                 
                 (if-not (nil? metadata)
                   (let [xmp-file (doto (java.io.File/createTempFile "pdfbox-xmp-" ".xml") .deleteOnExit) ]
                     (with-open [o (io/output-stream xmp-file)]
                       (.save (.exportXMPMetadata metadata) o)
                       )
                     (xml/cdata (slurp xmp-file))
                     )
                   )
                 )
    (catch Exception e 
      (xml/element :result {}
                 (xml/element :extractor {}
                              (xml/element :name {} "PDFBox Apache.org")
                              (xml/element :version {} (Version/getVersion))
                              )
                 (xml/element :identification {}
                              (xml/element :fileSize {} (format "%s" (.length test-file)))
                              (xml/element :filePath {} (format "%s" (.getAbsolutePath test-file)))
                              (xml/element :lastModified {} (format-date (.lastModified test-file)))
                              (xml/element :created {} "")
                              (xml/element :trapped {} "")
                              )
                 (xml/element :characterization {}
                              (xml/element :isEncrypted {} "")
                              (xml/element :numOfPages {} "")
                              (xml/element :author {} "")
                              (xml/element :title {} "")
                              (xml/element :subject {} "")
                              (xml/element :keywords {} "")
                              (xml/element :creator {} "")
                              (xml/element :producer {} "")
                              )
                 (xml/element :validation {}
                              (xml/element :isValidPDF {} (format "%s" false))
                              (xml/element :isValidPDFA {} (format "%s" false))
                              (xml/element :validationErrors {}
                                           (xml/element :error {:errorCode "exception"}
                                                        (normalize-string (.getMessage e))
                                                        )
                                           )
                              )
                 
                 )
      )
    (catch java.lang.NoClassDefFoundError e
      (xml/element :result {}
                 (xml/element :extractor {}
                              (xml/element :name {} "PDFBox Apache.org")
                              (xml/element :version {} (Version/getVersion))
                              )
                 (xml/element :identification {}
                              (xml/element :fileSize {} (format "%s" (.length test-file)))
                              (xml/element :filePath {} (format "%s" (.getAbsolutePath test-file)))
                              (xml/element :lastModified {} (format-date (.lastModified test-file)))
                              (xml/element :created {} "")
                              (xml/element :trapped {} "")
                              )
                 (xml/element :characterization {}
                              (xml/element :isEncrypted {} "")
                              (xml/element :numOfPages {} "")
                              (xml/element :author {} "")
                              (xml/element :title {} "")
                              (xml/element :subject {} "")
                              (xml/element :keywords {} "")
                              (xml/element :creator {} "")
                              (xml/element :producer {} "")
                              )
                 (xml/element :validation {}
                              (xml/element :isValidPDF {} (format "%s" false))
                              (xml/element :isValidPDFA {} (format "%s" false))
                              (xml/element :validationErrors {}
                                           (xml/element :error {:errorCode "exception"}
                                                        (normalize-string (pr-str e))
                                                        )
                                           )
                              )
                 )
      )
    (finally 
      (try 
        (.close pddocument)
        (catch Exception e)
        )
      )
    )
  )

(defn xml-with-elapsed-time [test-file]
  (let* [ start (. java.lang.System (nanoTime))
         xmldata (validate test-file)
         stop (. java.lang.System (nanoTime))
         ]
        xmldata
        )
  )


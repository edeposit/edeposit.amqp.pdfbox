(ns edeposit.amqp.pdfbox.utils
  )

(defn list-methods [object]
  (-> object .getClass .getDeclaredMethods)
  )

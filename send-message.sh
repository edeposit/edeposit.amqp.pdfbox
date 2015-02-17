#!/bin/bash
cat resources/validate-request-with-test-pdf.json | amqp-publish -e 'validate' -p -r 'request' -C 'edeposit/pdfbox-validate' -C 'edeposit/pdfbox-validate' -E 'application/json' -u 'amqp://guest:guest@localhost/pdfbox'
cat resources/validate-request-with-corrupted.json | amqp-publish -e 'validate' -p -r 'request' -C 'edeposit/pdfbox-validate' -C 'edeposit/pdfbox-validate' -E 'application/json' -u 'amqp://guest:guest@localhost/pdfbox'
cat resources/validate-request-with-test-pdfa-1b.json | amqp-publish -e 'validate' -p -r 'request' -C 'edeposit/pdfbox-validate' -C 'edeposit/pdfbox-validate' -E 'application/json' -u 'amqp://guest:guest@localhost/pdfbox'

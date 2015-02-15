#!/bin/bash
cat resources/validate-request.json | amqp-publish -e 'validate' -p -r 'request' -C 'edeposit/pdfbox-validate' -C 'edeposit/pdfbox-validate' -E 'application/json' -u 'amqp://guest:guest@localhost/pdfbox'

# edeposit.amqp.pdfbox
![travis status](https://travis-ci.org/edeposit/edeposit.amqp.pdfbox.png)

A Clojure library designed to validate PDF/A and extract informations from PDF

It uses pdfbox to validate.

It offers amqp service and cli.

## Usage

### validate file

lein run -- --file FILE_NAME

### run as amqp service

lein run -- --amqp

## Message structure

You can see message structure to send to AMQP at directory resources.

Or at script send-message.sh

## License

Copyright © 2013 Jan Stavel stavel.jan at gmail.com

Distributed under the Eclipse Public License, the same as Clojure.

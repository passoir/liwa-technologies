# Introduction #

Web Archive UI Framework Kit is a client-side js-html-css framework that helps creating user interface helpers for Web Archive browsing.

A very little php is used here for the demo purpose most functions read JSON data to build on-screen elements.

Functional specification are described in:
/documentation/webArchiveUIKit.dtd

This version covers the metabrowser, the timebrowser, and the organizer (folders only) modules.


## The Metabrowser ##

It can be either:

  * embedded in the archive page (banner model)
> this requires server-side archive html modification
  * a web page showing the archive in an iframe (full metabrowser)


## The Time Browser ##

It has several display options:

  * real annual calendar, with months and proportional height
  * compact calendar, with months instance lists
  * simple annual list
  * other options for labels (see html comments)

Other on-screen elements are supported (breadcrumb, title, original url, description, timestamp, simple navigation, etc.)

See: /documentation/webArchiveUIKit.dtd

Everything is (and should be) very CSS customizable.



# How-to start a demo ? #

  1. install on a LAMP server
  1. complete the server paths in /demo/config.php
  1. to launch the embeded version: /demo/embedded/index.php
  1. to launch the full metabrowser: /demo/metabrowser/index.php this launcher is populated with sample JSON data: /demo/json\_tables\_examples.php
  1. now you can adapt to your server side language and programs.JSON specs for your APIs are detailed in /UIF\_metabrowser.php comments
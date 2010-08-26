UIF Kit V1 Read me
------------------

Copyright 2010 LiWA

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at 
http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, 
software distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
See the License for the specific language governing permissions and limitations under the License. 




*****
About
*****


UIF Kit is a client-side js-html-css framework that helps
creating user interface helpers for Web Archive browsing.

A very little php is used here for the demo purpose
most functions read JSON data to build on-screen elements

Functional specification are described in
/documentation/webArchiveUIKit.dtd

V1 covers metabrowser, timebrowser, organizer (folders only)

¶ Metabrowser can be either :

- embedded in the archive page (banner model) 
  this requires server-side archive html modification
- a web page showing the archive in an iframe (full metabrowser)

¶ Time Browser has several display options :

- real annual calendar, with months and proportional height
- compact calendar, with months instance lists
- simple annual list
- other options for labels (see html comments)

¶ Other on-screen elements are supported 
  (breadcrumb, title, original url, description, timestamp, simple navigation…) 
  see /documentation/webArchiveUIKit.dtd
  
  
¶ Everything is (and should be) very CSS customizable.
  
  
  
********************
How-to start a demo ?
********************


1/ install on a LAMP server

2/ complete the server paths in /demo/config.php 

3/ to launch the embeded version : /demo/embedded/index.php

3b/ to launch the full metabrowser : /demo/metabrowser/index.php
	this launcher is populated with sample JSON data :
	/demo/json_tables_examples.php

4/ now you can adapt to your server side language and programs.
   JSON specs for your APIs are detailed in /UIF_metabrowser.php comments
   
   
   
   

*****************************
Files tree for implementation
*****************************


html is build with js functions parsing JSON data
3 JSON tables for input are detailed in /UIF_metabrowser.php comments


/UIF_embedded_banner.php *** html structure for the banner version and simple JSON table spec

/UIF_metabrowser.php *** html structure for the full metabrowser and full JSON tables spec

/demo/ *** is the php functional demo launcher

	/demo/config.php *** insert here the server paths

	/demo/embedded/ *** the embedded banner demo
		/demo/embedded/index.php
		/demo/embedded/UIF_tools_php_embedded.php

	/demo/json_tables_examples.php ***sample JSON data

	/demo/metabrowser/ *** the full metabrowser demo
		/demo/metabrowser/archiveReader.php
		/demo/metabrowser/index.php
		/demo/metabrowser/UIF_metabrowser_php.php

/documentation/  *** specs

	/documentation/webArchiveUIKit.xml
	/documentation/webArchiveUIKit.dtd

/helpers/  *** third party helpers 

	/helpers/blankCore/
	/helpers/iepngfix/
	/helpers/jquery-ui-1.8.custom/
	/helpers/jQuery-hoverindent/

/scripts/  *** core scripts (JQuery based)
	/scripts/htmlOutput.js
	/scripts/scripts.js

/themes/ *** look & feel customization

	/themes/UIKitTheme/  *** default theme
		/themes/UIKitTheme/css
		/themes/UIKitTheme/images

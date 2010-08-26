<!--
Copyright 2010 LiWA

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at 
http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, 
software distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
See the License for the specific language governing permissions and limitations under the License. 
-->

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="fr">
<head>
	<meta name="generator" content="HTML Tidy for Mac OS X (vers 14 February 2006), see www.w3.org" />
	<meta http-equiv="content-type" content="text/html; charset=utf-8" />
	<meta name="author" content="Living Web Archives - http://www.liwa-project.eu" />
	<meta name="keywords" content="" />
	<title>UI Framework</title>
	<link rel="shortcut icon" type="image/x-icon" href="themes/UIKitTheme/images/uikfav.png" />
	<link rel="stylesheet" href="helpers/blankCore/css/blank.css" type="text/css" />
	<link rel="stylesheet" href="themes/UIKitTheme/css/UIKitTheme.css" type="text/css" media="screen" />
	<link rel="stylesheet" href="themes/UIKitTheme/css/backoffice.css" type="text/css" media="screen" />
  	<link rel="stylesheet" href="themes/UIKitTheme/css/print.css" type="text/css" media="print" />
	<link rel="stylesheet" href="themes/UIKitTheme/css/calendar.css" type="text/css" media="screen" />
	<!--[if IE 6]> <link rel="stylesheet" type="text/css" media="screen" href="themes/UIKitTheme/css/debug-ie6.css"/> <![endif]-->
	<!--[if IE 7]> <link rel="stylesheet" type="text/css" media="screen" href="themes/UIKitTheme/css/debug-ie7.css"/> <![endif]-->
	<!--[if IE 8]> <link rel="stylesheet" type="text/css" media="screen" href="themes/UIKitTheme/css/debug-ie8.css"/> <![endif]-->
	<script type="text/javascript" src="helpers/jquery-ui-1.8.custom/js/jquery-1.4.2.min.js" ></script>
	<script type="text/javascript" src="helpers/jquery-ui-1.8.custom/js/jquery-ui-1.8.custom.min.js" ></script>
	<script type="text/javascript" src="scripts/scripts.js" ></script>
	<script type="text/javascript" src="scripts/htmlOutput.js" ></script>
	<script type="text/javascript" src="helpers/jQuery-hoverIndent/jquery.hoverIntent.minified.js" ></script>

<script type="text/javascript">
	// complete by server-side program
	jsonArchiveInfos = '';
		
	/** JSON format for archive informations **/
	/*
	(each information is optional)
	
	{
	//title of the  archived website
	"title" : title,
	// original URL of the archived website
	"originalUrl" : original url,
	// timestamp of the current archive unit (optional)
	"timestamp" : timestamp,
	// description of the archived website (optional)
	"description" : description of the archive,
	// breadcrumb of the website in collections
	// for each collection, precise its label and its link to access it (optional)
	"breadcrumb" : {"collection  : "url to collection",
					"sub collection" : "url to sub collection",
					"sub sub collection" : "url to sub sub collection"},
	// link to the previous archive unit (optional)
	"prevUrl" : url,
	// link to the next archive unit (optional)
	"nextUrl" : url,
	// link to the first archive unit (optional)
	"firstUrl" : url,
	// link to the last archive unit (optional)
	"lastUrl" : url,
	// link to the page where are located all the instances of the archived website (optional)
	"allUrls" : url
	}
	*/
</script>

</head>
<body class="embedded">

	<div id="metabrowser">
		<div id="metabrowserTop">
			<!-- brand name and its logo -->
			<div id="branding"><a href="javascript:void(0)" title="The webarchive name"><span class="accessibility">The webarchive name</span></a></div>      
   			<!-- archive metadata -->
    		<script type="text/javascript">
    			document.write(createMetadataSnippet( eval('(' + jsonArchiveInfos + ')' ) ));
			</script>		
			<div class="clearer">&nbsp;</div>
		</div><!-- end #metabrowserTop -->
    
   		<!-- tools : time browser, organizer and page annotations -->
    	<div id="tools"> 
    	
      		<!-- navigation through some specific archive units -->      
      		<script type="text/javascript">
    				document.write( createNavigationSnippet( eval('(' + jsonArchiveInfos + ')' ) ));
	  		</script>

    	</div><!-- end #tools -->
	</div><!-- end #metabrowser -->  
    
</body>
</html>

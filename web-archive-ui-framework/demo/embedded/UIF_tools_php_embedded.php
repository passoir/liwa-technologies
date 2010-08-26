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

<?php require_once('../config.php'); ?>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="fr">
<head>
	<meta name="generator" content="HTML Tidy for Mac OS X (vers 14 February 2006), see www.w3.org" />
	<meta http-equiv="content-type" content="text/html; charset=utf-8" />
	<meta name="author" content="Living Web Archives - http://www.liwa-project.eu" />
	<meta name="keywords" content="" />
	<title>UI Framework</title>
	<link rel="shortcut icon" type="image/x-icon" href="<?php echo THEMES_UIKIT_URL;?>images/uikfav.png" />
	<link rel="stylesheet" href="<?php echo HELPERS_URL;?>blankCore/css/blank.css" type="text/css" />
	<link rel="stylesheet" href="<?php echo THEMES_UIKIT_URL;?>css/UIKitTheme.css" type="text/css" media="screen" />
	<link rel="stylesheet" href="<?php echo THEMES_UIKIT_URL;?>css/backoffice.css" type="text/css" media="screen" />
  	<link rel="stylesheet" href="<?php echo THEMES_UIKIT_URL;?>css/print.css" type="text/css" media="print" />
	<link rel="stylesheet" href="<?php echo THEMES_UIKIT_URL;?>css/calendar.css" type="text/css" media="screen" />
	<!--[if IE 6]> <link rel="stylesheet" type="text/css" media="screen" href="<?php echo THEMES_UIKIT_URL;?>css/debug-ie6.css"/> <![endif]-->
	<!--[if IE 7]> <link rel="stylesheet" type="text/css" media="screen" href="<?php echo THEMES_UIKIT_URL;?>css/debug-ie7.css"/> <![endif]-->
	<!--[if IE 8]> <link rel="stylesheet" type="text/css" media="screen" href="<?php echo THEMES_UIKIT_URL;?>css/debug-ie8.css"/> <![endif]-->
	<script type="text/javascript" src="<?php echo HELPERS_URL;?>jquery-ui-1.8.custom/js/jquery-1.4.2.min.js" ></script>
	<script type="text/javascript" src="<?php echo HELPERS_URL;?>jquery-ui-1.8.custom/js/jquery-ui-1.8.custom.min.js" ></script>
	<script type="text/javascript" src="<?php echo HELPERS_URL;?>jQuery-hoverIndent/jquery.hoverIntent.minified.js" ></script>
	<script type="text/javascript" src="<?php echo SCRIPTS_URL;?>scripts.js" ></script>
	<script type="text/javascript" src="<?php echo SCRIPTS_URL;?>htmlOutput.js" ></script>
	
	<?php 
		$archive = $_GET['archive'];
		// completed by server-side program
		$jsonArchiveInfos = '{"title":"Department of Health","originalUrl":"http:\/\/www.dh.gov.uk\/en\/index.htm","timestamp":"20040209012729","description":"UK Department of Health : Policy, guidance and publications for NHS and social care professionals","breadcrumb":{"Media":"http:\/\/www.europarchive.org\/index.php","Web":"http:\/\/www.europarchive.org\/web.php","UKGov PRO Weekly Crawl":"http:\/\/www.europarchive.org\/ukgov.php"},"prevUrl":"http:\/\/web.archive.org\/web\/20030324211731\/http:\/\/doh.gov.uk\/","nextUrl":"http:\/\/web.archive.org\/web\/20030410161137\/http:\/\/doh.gov.uk\/","firstUrl":"http:\/\/web.archive.org\/web\/19990125094844\/http:\/\/www.doh.gov.uk\/","lastUrl":"http:\/\/web.archive.org\/web\/20080411204705\/http:\/\/www.doh.gov.uk\/","allUrls":"http:\/\/web.archive.org\/web\/*\/http:\/\/www.doh.gov.uk\/"}';
	?>
	<script type="text/javascript">
		// completed by server-side program
		jsonArchiveInfos = '<?php echo $jsonArchiveInfos; ?>';
	</script>

</head>
<body class="embedded">

	<div id="metabrowser">
		<div id="metabrowserTop">
			<!-- brand name and its logo -->
			<div id="branding"><a href="javascript:void(0)" title="The webarchive name"><span class="accessibility">The webarchive name</span></a></div>      
   			<!-- archive metadata -->
    		<script type="text/javascript">
    			document.write(createMetadataSnippet( eval('(' + jsonArchiveInfos + ')') ));
			</script>		
			<div class="clearer">&nbsp;</div>
		</div><!-- end #metabrowserTop -->
    
   		<!-- tools : time browser, organizer and page annotations -->
    	<div id="tools"> 
    	
      		<!-- navigation through some specific archive units -->      
      		<script type="text/javascript">
    				document.write( createNavigationSnippet( eval('(' + jsonArchiveInfos + ')') ));
	  		</script>
        
    	</div><!-- end #tools -->
	</div><!-- end #metabrowser -->  
    
</body>
</html>

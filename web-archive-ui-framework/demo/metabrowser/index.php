<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<meta http-equiv="content-type" content="text/html; charset=utf-8" />
	<link rel="shortcut icon" type="image/x-icon" href="themes/UIKitTheme/images/uikfav.png" />
	<title>UI Framework</title>
</head>
<body>

<form action="archiveReader.php" method="post">
	<label for="archivedPage">archived page URL</label><br/>
	<input type="text" id="archivedPage" name="archivedPage" value="http://web.archive.org/web/20030409093022/www.doh.gov.uk/" />
	<br/><br/>

	<label for="archiveInfos">JSON archive informations</label><br/>
	<textarea id="archiveInfos" name="archiveInfos" cols="60" rows="10">{"title":"Department of Health","originalUrl":"http:\/\/www.dh.gov.uk\/en\/index.htm","timestamp":"20040209012729","description":"UK Department of Health : Policy, guidance and publications for NHS and social care professionals","breadcrumb":{"Media":"http:\/\/www.europarchive.org\/index.php","Web":"http:\/\/www.europarchive.org\/web.php","UKGov PRO Weekly Crawl":"http:\/\/www.europarchive.org\/ukgov.php"},"prevUrl":"http:\/\/web.archive.org\/web\/20030324211731\/http:\/\/doh.gov.uk\/","nextUrl":"http:\/\/web.archive.org\/web\/20030410161137\/http:\/\/doh.gov.uk\/","firstUrl":"http:\/\/web.archive.org\/web\/19990125094844\/http:\/\/www.doh.gov.uk\/","lastUrl":"http:\/\/web.archive.org\/web\/20080411204705\/http:\/\/www.doh.gov.uk\/","allUrls":"http:\/\/web.archive.org\/web\/*\/http:\/\/www.doh.gov.uk\/"}</textarea>
	<br/><br/>
	
	<label for="archiveInstances">JSON archive instances</label><br/>
	<textarea id="archiveInstances" name="archiveInstances" cols="60" rows="10">{"2002":[{"timestamp":"20020206034401","url":"http:\/\/web.archive.org\/web\/20020206034401\/http:\/\/www.doh.gov.uk\/index.html"},{"timestamp":"20020505173223","url":"http:\/\/web.archive.org\/web\/20020505173223\/http:\/\/www.doh.gov.uk\/index.html"},{"timestamp":"20020505223223","url":"http:\/\/web.archive.org\/web\/20020505223223\/http:\/\/www.doh.gov.uk\/index.html"},{"timestamp":"20020506135248","url":"http:\/\/web.archive.org\/web\/20020506135248\/http:\/\/www.doh.gov.uk\/"},{"timestamp":"20020531135248","url":"http:\/\/web.archive.org\/web\/20020531135248\/http:\/\/www.doh.gov.uk\/"},{"timestamp":"20020604063514","url":"http:\/\/web.archive.org\/web\/20020604063514\/http:\/\/www.doh.gov.uk\/"},{"timestamp":"20020604213514","url":"http:\/\/web.archive.org\/web\/20020604213514\/http:\/\/www.doh.gov.uk\/"}],"2001":[],"2000":[{"timestamp":"20000229162440","url":"http:\/\/web.archive.org\/web\/20000229162440\/http:\/\/www.doh.gov.uk\/"},{"timestamp":"20000304071208","url":"http:\/\/web.archive.org\/web\/20000304071208\/http:\/\/www.doh.gov.uk\/"},{"timestamp":"20000511062510","url":"http:\/\/web.archive.org\/web\/20000511062510\/http:\/\/www.doh.gov.uk\/"},{"timestamp":"20000511089350","url":"http:\/\/web.archive.org\/web\/20000511089350\/http:\/\/www.doh.gov.uk\/"},{"timestamp":"20000815073754","url":"http:\/\/web.archive.org\/web\/20000815073754\/http:\/\/www.doh.gov.uk\/"}],"1999":[{"timestamp":"19990125094844","url":"http:\/\/web.archive.org\/web\/19990125094844\/http:\/\/www.doh.gov.uk\/"},{"timestamp":"19990208012734","url":"http:\/\/web.archive.org\/web\/19990208012734\/http:\/\/www.doh.gov.uk\/"},{"timestamp":"19990421061832","url":"http:\/\/web.archive.org\/web\/19990421061832\/http:\/\/www.doh.gov.uk\/"},{"timestamp":"19990428231415","url":"http:\/\/web.archive.org\/web\/19990428231415\/http:\/\/www.doh.gov.uk\/"},{"timestamp":"19991003034823","url":"http:\/\/web.archive.org\/web\/19991003034823\/http:\/\/www.doh.gov.uk\/"},{"timestamp":"19991104023232","url":"http:\/\/web.archive.org\/web\/19991104023232\/http:\/\/www.doh.gov.uk\/"}]}</textarea>
	<br/><br/>
	
	<label for="folderList">JSON folder list</label><br/>
	<textarea id="folderList" name="folderList" cols="60" rows="10">[{"type":"folder","label":"UK government","creator":"Nicolas","timestamp":"XXXX-XX-XX XX:XX UTC","configUrl":"","children":[{"type":"folder","label":"Health","creator":"Julien","timestamp":"XXXX-XX-XX XX:XX UTC","configUrl":"","children":[{"type":"archiveUnit","label":"Department of Health","creator":"Nicolas","description":"UK Department of Health : Policy, guidance and publications for NHS and social care professionals","timestamp":"XXXX-XX-XX XX:XX UTC","url":"http:\/\/web.archive.org\/web\/*\/http:\/\/www.doh.gov.uk\/","configUrl":""},{"type":"archiveUnit","label":"Department for culture, media and sport","creator":"Julien","description":"We are the Department responsible for the 2012 Olympic Games and Paralympic Games, and we help drive the Digital Economy. Our aim is to improve the quality of life for all through cultural and sporting activities, to support the pursuit of excellence and to champion the tourism, creative and leisure industries.","timestamp":"XXXX-XX-XX XX:XX UTC","url":"http:\/\/collections.europarchive.org\/tna\/*\/http:\/\/www.culture.gov.uk\/","configUrl":""},{"type":"archiveUnit","label":"Wired for Health","creator":"Julien","timestamp":"XXXX-XX-XX XX:XX UTC","url":"http:\/\/collections.europarchive.org\/tna\/*\/http:\/\/www.wiredforhealth.gov.uk\/","configUrl":""}]},{"type":"archiveUnit","label":"10 Downing street","creator":"Nicolas","description":"The official site of the Prime Minister\'s Office","timestamp":"XXXX-XX-XX XX:XX UTC","url":"http:\/\/collections.europarchive.org\/tna\/*\/http:\/\/number10.gov.uk\/","icon":"http:\/\/www.number10.gov.uk\/favicon.ico","configUrl":""}]}]</textarea>
	<br/><br/>
	
	<input type="submit" value="read archive" />
</form>

</body>
</html>

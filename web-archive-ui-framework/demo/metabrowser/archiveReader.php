<?php

$archivedPage = $_POST['archivedPage'];
$jsonArchiveInfos = $_POST['archiveInfos'];
$jsonArchiveInstances = $_POST['archiveInstances'];
$jsonFolderList = $_POST['folderList'];
$iframeCode = '<iframe id="mainFrame" name="mainFrame" src="' . $archivedPage . '" />';

ob_start();
	require_once("UIF_metabrowser_php.php");
 	$html = ob_get_contents();
ob_end_clean();

echo $html;

?>
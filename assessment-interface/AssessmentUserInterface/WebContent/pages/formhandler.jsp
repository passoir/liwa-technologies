<script type="text/javascript">
// This and the next 5 lines were generated from a config xml file by  de.l3s.liwa.assessment.Configuration.java
var ruleStr = ";;;;;;;;;;;;;;10:Media,7:Educational,11:Database,5:News-Edit,6:Commercial;10:Media,7:Educational,5:News-Edit,6:Commercial;10:Media,7:Educational,11:Database,5:News-Edit,6:Commercial;";
var defStr = "HTNormal;en;NonAdult;NoOtherProblem;NonSpam;;;;;;;;;;;;;";
var defValuesStr = "1-langForm:usersLanguage;";
var needsCommentStr = ";;;NoOtherProblem;;;;;;;;;;;;;;Sure";
var selectMinOneStr = ";;;;;3:NonNews-Edit;3:NonCommercial;3:NonEducational;3:NonDiscussion;3:NonPersonal-Leisure;3:NonMedia;3:NonDatabase;;;;;;";
// global variable : 2-dimensional array for the rules strings
var rule2DArray;

// global variable : array, contains default values, but
// only for menus that have the property that if not the default is selected
// then all other menus should be disabled
var defaultsArray;

// contains the default values at indexes where a non-default selected value
// requires the comment to be filled by the user
var needsCommArr;

// contains "groupId:defaultValue" info for menus that are part of a
// menu-group that has the property that at least one menu must
// be set to a nondefault value in the group.
var selectMinOneArr;
// contains group id-s for the above
var groupIdArr;


function computeRules() {
    // fill defaults2DArray
    // (defStr will be genereated and added to the beginning of this file,
    // containing the default values)
    var defList = defStr.split(";");
    defaultsArray = new Array(defList.length);
    for(var i = 0; i < defList.length; i++) {
        if( !isUndefined(defList[i]) && defList[i].length > 0) {
            defaultsArray[i] = defList[i];
        }
    }

    // fill rule2DArray
    // (ruleStr will be genereated and added to the beginning of this file,
    // containing the rules)
    var ruleList = ruleStr.split(";");
    rule2DArray = new Array(ruleList.length);
    for(var i = 0; i < ruleList.length; i++) {
        if( !isUndefined(ruleList[i]) && ruleList[i].length > 0) {
            // requirements (OR connection)
            var oneRuleList = ruleList[i].split(",");
            rule2DArray[i] = new Array(oneRuleList.length);
            for(var j = 0; j < oneRuleList.length; j++) {
                rule2DArray[i][j] = oneRuleList[j];
            }
        }
    }

    var ncList = needsCommentStr.split(";");
    needsCommArr = new Array(ncList.length);
    for(var i = 0; i < ncList.length; i++) {
        if( !isUndefined(ncList[i]) && ncList[i].length > 0) {
            needsCommArr[i] = ncList[i];
        }
    }

    var smoList = selectMinOneStr.split(";");
    selectMinOneArr = new Array(smoList.length);
    grpIdArr = new Array();
    var lastGrpId = -1;
    for(var i = 0; i < smoList.length; i++) {
        if( !isUndefined(smoList[i]) && smoList[i].length > 0) {
            selectMinOneArr[i] = smoList[i];
            var splitted = smoList[i].split(":");
            if (!(lastGrpId == splitted[0])) {
                // fill next val
                grpIdArr[grpIdArr.length] = splitted[0];
                lastGrpId = splitted[0];
            }
        }
    }

    var defValsList = defValuesStr.split(";");
    for(var i = 0; i < defValsList.length-1; i++) {
        var defValSplitted = defValsList[i].split("-");
        var index = defValSplitted[0];
        var hiddenFieldId = defValSplitted[1];

        // find the field within the page
        var field = document.getElementById('langForm:usersLanguage');
        if(!(field == null) && (field.value != "")) {
            // set default value
            defaultsArray[index] = field.value;

            // set content of menu to display default
            var toSetParent = document.getElementById('navigation:labelsdatatable');
            var children = toSetParent.getElementsByTagName('select');
            var toSelectIndex = -1;
            for (var j=0; j<children[index].options.length; j++ ) {
                if (children[index].options[j].value == field.value) {
                    toSelectIndex = j;
                }
            }
            children[index].options[toSelectIndex].selected=true;
        }
    }
}

function checkSaveRequirements() {
    // this form contains the menus with labels to be chosen
    var formParent = document.getElementById('navigation:labelsdatatable');
    // the selected labels can be obtained from here:
    var children = formParent.getElementsByTagName('select');

    // check if at least one is set to non-default from a set of labels
    for (var i=0; i<grpIdArr.length; i++) {
        var grpId = grpIdArr[i];
        var isOk = false; // will be true if a nondefault is selected
        var disabledCnt = 0;
        var allCnt = 0;
        for (var j=0; j<selectMinOneArr.length; j++) {
            if (!(isUndefined(selectMinOneArr[j]))
                && selectMinOneArr[j].length > 0) {

                var splitted = selectMinOneArr[j].split(":");
                if (splitted[0] == grpId) {
                    allCnt++;
                    // check if here a nondefault is selected
                    if (children[j].disabled==false) { // check active values only
                        var selectMenu = children[j];
                        var selectedVal =
                            selectMenu.options[selectMenu.selectedIndex].value;
                        if (!(selectedVal==splitted[1])) {
                            isOk = true;
                        }
                    } else {
                        disabledCnt++;
                    }
                }
            }
        }
        // if every item was disabled: it must be ok
        if (allCnt == disabledCnt) {
            isOk = true;
        }

        if (isOk == false) {
            alert("Please select at least one content type (for example "
                  + "News, Educational, etc.) or set 'Other Problem' and "
                  +"describe why you can not assess the page.");
            return false;
        }
    }
    // check : if one special option is selected then the comment-textfield
    // must be non-empty
    var commentStr =
        document.getElementById('navigation:buttonsForm:comment').value;

    for (var i=0; i<needsCommArr.length; i++) {
        if( !(isUndefined(needsCommArr[i])) && needsCommArr[i].length > 0) {
            // check if we have a different value set
            if (children[i].disabled==false) { // check active values only
                var selectMenu = children[i];
                var selectedVal =
                    selectMenu.options[selectMenu.selectedIndex].value;
                if (!(selectedVal==needsCommArr[i])) {

                    if ((commentStr==null) || (commentStr=="")) {
                        alert("Please fill the Comment field! Give a "
                              + "reason why you are unsure/can not assess this page.");
                        return false;
                    }
                }
            }
        }
    }
    return true;
}

function collectLabels() {
    // check form requirements
    var ok = checkSaveRequirements();

    if (ok == true) {
        // this form contains the menus with labels to be chosen
        var formParent = document.getElementById('navigation:labelsdatatable');
        // the selected labels can be obtained from here:
        var children = formParent.getElementsByTagName('select');
        // we  will collect data (chosen label strings) into this
        var collectedStr = "";
        var i;
        for(i = 0; i < children.length; i++) {
            if (children[i].disabled==false) { // save active values only
                var selectMenu = children[i];
                var selectedVal =
                    selectMenu.options[selectMenu.selectedIndex].value;
                collectedStr += (selectedVal + ";");
            } else {
                collectedStr += ";";
            }
        }
        // add comment (from a textbox)
        var commentstr = document
            .getElementById('navigation:buttonsForm:comment').value + ";";
        // replace '-s because they affect the sql query
        var comment = commentstr.replace("'","");
        // replace ";"-s because they work as a separator
        comment = comment.replace(";",",");
        collectedStr += comment;
        return collectedStr;
    } else {
        return "undefined";
    }
}

function collectAndSaveLabels() {
    var collectedStr = collectLabels();
    if (collectedStr=="undefined") {
        return false;
    }    
    // check if the last label was saved
    var lastSaveSuccess = document.getElementById('saveSuccessForm:saveSuccessField').value;
    if (!(lastSaveSuccess=="true")) {
        alert("Error: your last assessment could not be saved. "
              + "There may be a database error.\n"
              + "Please inform 'aszabo /at/ ilab.sztaki.hu'.");
    }
    // set the hidden textfield to contain all the collected data
    var toSet = document.getElementById('myInputForm:myTextField');
    toSet.value = collectedStr;
    // send data
    document.getElementById('myInputForm:myCommandButton').click();
    return false;
}

// The same as above, but another button has to be clicked at the end
function collectAndSaveLabelsBack() {
    var collectedStr = collectLabels();
    if (collectedStr=="undefined") {
        return false;
    }
    // check if the last label was saved
    var lastSaveSuccess = document.getElementById('saveSuccessForm:saveSuccessField').value;
    if (!(lastSaveSuccess=="true")) {
        alert("Error: your last assessment could not be saved. There may be a database error.");
    }
    var toSet = document.getElementById('myInputFormBack:myTextFieldBack');
    toSet.value = collectedStr;
    document.getElementById('myInputFormBack:myCommandButtonBack').click();
    return false;
}

// Returns the index (-1 if not relevant),
// of the menu that was set to non-default value
function checkNonDefault(menuList) {
    var ret = -1;
    for(var i = 0; i < menuList.length; i++) {
        if (!isUndefined(defaultsArray[i]) && defaultsArray[i].length > 0) {
            var selectedVal =
                menuList[i].options[menuList[i].selectedIndex].value;
            // check if a non-default value is selected
            if (selectedVal != defaultsArray[i]) {
                ret = i;
                return ret;
            }
        }
    }
    return ret;
}

function setMenusDisabled() {
    var toSetParent = document.getElementById('navigation:labelsdatatable');
    var children = toSetParent.getElementsByTagName('select');

    // check the defaults part
    var indexOfNonDefault = checkNonDefault(children);
    //alert("indexOfNonDefault: " + indexOfNonDefault);
    if (indexOfNonDefault >= 0) {
        // disable all menuitems, except the one
        for(var i = 0; i < children.length; i++) {
            children[i].disabled = true;
        }
        children[indexOfNonDefault].disabled = false;
    } else { // first enable all
        for(var i = 0; i < children.length; i++) {
            children[i].disabled = false;
        }
        // and let the other dependencies take effect
        // set inter-dependant menus
        for(var i = 0; i < children.length; i++) {
            var selectMenu = children[i];
            if (!isUndefined(rule2DArray[i]) && rule2DArray[i].length > 0) {
                var canBeEnabled = false;
                for (var j=0; j<rule2DArray[i].length ;j++) {
                    var requirementForMenu = rule2DArray[i][j];
                    if (checkRequirement(requirementForMenu, children)) {
                        canBeEnabled = true;
                    }
                }
                // set
                if (canBeEnabled) {
                    children[i].disabled=false;
                } else {
                    children[i].disabled=true;
                }
            }
        }
    }
    return false;
}

function checkRequirement(reqString, menuList) {
    var splitted = reqString.split(":");
    // first element is the number of child in menuList to check
    // second is the required value
    if (menuList[splitted[0]].value == splitted[1]) {
        return true;
    } else {
        return false;
    }
}

function isUndefined(x) { return x == null && x !== null; }

function truncUrl(fullUrl) {
    var h = fullUrl.indexOf('#');
    if (h >= 0) {
        return fullUrl.substring(h+1);
    } else {
        return "";
    }
}

function getHostNameFromUrl(url) {
    // cut at the third "/"
    var splitted = url.split("/");
    // put together the first 3 parts
    var ret = splitted[0] + "/"+ splitted[1]+"/"+splitted[2];
    return ret;
}

function loadLivePage(event) {
    if(event == null) event = window.event;
    var mev =  (typeof event.target != 'undefined') ? event.target : event.srcElement;
    var eventtype=event.type;
    var children=mev.childNodes;
    var url=trim(children[0].nodeValue);
    mev.href=url;
    mev.onclick();
}

function catchevent(event) {
    // for the iframes communication hack - getting the iframe's
    // loaded url from the main window's url after "#"
    var loadedUrl = truncUrl(window.location.href);
    if (loadedUrl.length > 1) {
        var newHostName = trim(getHostNameFromUrl(loadedUrl));
        var origHost = document.getElementById("currentHost:hostUrl");
        if(!(origHost == null) && (origHost.value != "")
           && !(newHostName == null) && (newHostName != "")) {
            var origHostName = trim(origHost.value);
            if (origHostName != newHostName) {
                // check if only a "www." is the difference
                if (origHostName.replace("www\.","") == newHostName) {
                    // do nothing
                } else if (newHostName.replace("www\.","") == origHostName) {
                    // do nothing
                } else {
                    // alert("You are viewing another host than the host "
                    //      + "under assessment! (Or you were redirected.)");
                    if (confirm("You are viewing another host than the host "
                                + "under assessment! (Or you were redirected.)")) {
                        // change contents of the tabbed info pane
                        generatedRerenderFunction(newHostName);
                    } else {
                        // do nothing
                    }
                }
            }
        }
    }
}

function viewPage(event) {
    if(event == null) event = window.event;
    var mev = (typeof event.target != 'undefined') ?
        event.target : event.srcElement;
    var url = trim(mev.getAttribute('title'));
    var myiframe = document.getElementById("rf");
    myiframe.src = "http://194.109.159.7/qa_euspam/20100308191441/" + url;
}

function changeVHost(event) {
    if(event == null) event = window.event;
    var mev =  (typeof event.target != 'undefined') ?
        event.target : event.srcElement;
    var eventtype=event.type;
    var children=mev.childNodes;
    var url=trim(children[0].nodeValue);
    // load into frame
    var myiframe = document.getElementById("rf");
    myiframe.src="http://194.109.159.7/qa_euspam/20100308191441/" + url;
    // call generated js functon
    generatedRerenderFunction(url);
    return false;
}

function addHostUrlToUsersList() {
    var nextHostForm = document.getElementById("navigation:nextHostsForm");
    nextHostForm.submit();
    alert("Host saved to your assessment list.")
    nextHostForm.reset();
    return false;
}

function trim(str, chars) {
    return ltrim(rtrim(str, chars), chars);
}

function ltrim(str, chars) {
    chars = chars || "\\s";
    return str.replace(new RegExp("^[" + chars + "]+", "g"), "");
}

function rtrim(str, chars) {
    chars = chars || "\\s";
    return str.replace(new RegExp("[" + chars + "]+$", "g"), "");
}
</script>

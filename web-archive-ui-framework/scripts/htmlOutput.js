/*
Copyright 2010 LiWA

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at 
http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, 
software distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
See the License for the specific language governing permissions and limitations under the License. 
*/




// monthsAssoc table : associate month numbers with month labels
var monthsAssoc = new Array();
monthsAssoc['01'] = 'jan';
monthsAssoc['02'] = 'feb';
monthsAssoc['03'] = 'mar';
monthsAssoc['04'] = 'apr';
monthsAssoc['05'] = 'may';
monthsAssoc['06'] = 'jun';
monthsAssoc['07'] = 'jul';
monthsAssoc['08'] = 'aug';
monthsAssoc['09'] = 'sep';
monthsAssoc['10'] = 'oct';
monthsAssoc['11'] = 'nov';
monthsAssoc['12'] = 'dec';

/*
* createMetadataSnippet : creates the HTML snippet of the archive metadata
* param : json table
*/
function createMetadataSnippet(json) {
	var html = "<div id=\"metadata\">\n";
	
	// breadcrumb of the collections in which is contained the archive
	if ( json['breadcrumb'] != undefined ) {
		html += "<span id=\"archiveBreadCrumb\">\n";
		html += "You are here: ";
		for ( val in json['breadcrumb'] ) {
			html += "<a href=\"" + json['breadcrumb'][val] + "\" target=\"_top\">" + val + "</a> / ";
		}
		html = html.slice(0, -2);
		html += "</span>\n";
	}
	
	// timestamp of the current archive unit
	if ( json['timestamp'] != undefined ) {
		var ts = json['timestamp'];
		// yyyy month dd, hh:mm:ss
		var date = ts.slice(0, 4) + ' ' + monthsAssoc[ts.slice(4, 6)] + ' ' + ts.slice(6, 8) + ', ' + ts.slice(8, 10) + ':' + ts.slice(10, 12) + ':' + ts.slice(12, 14);
		html += "<span id=\"archiveTimestamp\">" + date + "</span>\n";
	}
	
	// title of the archived website
	if ( json['title'] != undefined ) {
		html += "<span id=\"archiveTitle\">Title: " + json['title'] + "</span>\n";
	}
	
	// current url of the archived website
	if ( json['originalUrl'] != undefined ) {
		html += "<span id=\"archiveOriginalUrl\">Original URI: <a href=\"" + json['originalUrl'] + "\" target=\"_top\">" + json['originalUrl'] + "</a></span>\n";
	}
	// archived website description
	if ( json['description'] != undefined ) {
		html += "<span id=\"archiveDescription\">" + json['description'] + "</span>\n";
	}
	
	html += "<div class=\"spacer30\">&nbsp;</div>\n";
	html += "</div><!-- #metadata -->\n";
	return html;
}

/*
* createNavigationSnippet : creates the HTML snippet of the archive navigation
* param : json table
*/
function createNavigationSnippet(json) {
	// navigation links : First / Previous / All / Next / Last
	
	var html = "<div id=\"navigation\">\n";
	
	// link to the first archive unit (optional)
	if ( json['firstUrl'] != undefined ) {
		html += "<a href=\"" + json['firstUrl'] + "\" target=\"_top\">First</a>";
	}
	// link to the next archive unit (optional)
	if ( json['prevUrl'] != undefined ) {
		html += "<a href=\"" + json['prevUrl'] + "\" target=\"_top\">Previous</a>";
	}
	
	// opens the time browser drawer to see all the archive units
	html += "<a href=\"" + json['allUrls'] + "\" class=\"seeAll\" target=\"_top\">All</a>";
	
	// link to the next archive unit (optional) 
	if ( json['nextUrl'] != undefined ) {
		html += "<a href=\"" + json['nextUrl'] + "\" target=\"_top\">Next</a>";
	}
	// link to the last archive unit (optional)
	if ( json['lastUrl'] != undefined ) {
		html += "<a href=\"" + json['lastUrl'] + "\" target=\"_top\">Last</a>";
	}
	
	html += "</div>\n";
	html += "<div class=\"clearer\">&nbsp;</div>\n";
	return html;
}


/*
* createTimeBrowserSnippet : creates the HTML snippet of the archive time browser
* param : json table
*/
function createTimeBrowserSnippet(json) {
	html = '<div class="calendarSlider">';
	for ( instYear in json ) {
		/* year label */
		html += "<div class=\"bundle scale smallScale\">\n";
		html += "<div id=\"year" + instYear + "\" class=\"year\">\n";
		html += "<div class=\"label\">" + instYear + "</div>\n";
		// number of instances for this year
		html += "<div class=\"counter\">" + json[instYear].length + " inst.</div>\n";
		html += "<div class=\"clearer\">&nbsp;</div>\n";
		html += "<div class=\"monthCalendar\">\n";
		
		/* month calendar */
		for ( var m = 1; m <= 12; m++) {
			month = m + '';
			if ( month.length == 1 ) {
				month = '0' + month;
			}
			
			/* months list */
			html += "<div id=\"month" + instYear + month + "\" class=\"month\">\n";
			html += "<div class=\"label\">" + monthsAssoc[month] + "</div>\n";
			
			var instOfMonthTab = new Array();
			for ( var i = 0; i < json[instYear].length; i++ ) {
				var instance = json[instYear][i];				
				if ( (instance['timestamp']).search(eval('/^' + instYear + month + '/')) != -1 ) {
					instOfMonthTab.push(instance);
				}
			}
			// number of instances for this month
			html += "<div class=\"counter\">" + instOfMonthTab.length + " inst.</div>\n";
			html += "</div>\n";
		}
		
		/* instances list */
		html += "<div class=\"instanceList\">\n";
		
		// for each year, sort instances by dates
		var instancesDates = new Array();
		for ( var i = 0; i < json[instYear].length; i++ ) {
			var key = json[instYear][i]['timestamp'].slice(4, 8);
			if ( ! instancesDates[key] ) {
				instancesDates[key] = new Array(json[instYear][i]);
			} else {
				instancesDates[key].push(json[instYear][i]);				
			}
		}
		
		for ( key in instancesDates ) {
			var instance = instancesDates[key][0];
			var date = instance['timestamp'].slice(0, 8); // yyyymmdd
			var month = date.slice(4, 6);
			var monthLabel = monthsAssoc[month];
			var day = date.slice(6, 8);
				
			html += "<div id=\"instance" + date + "\" class=\"instance\">\n";
			
			if ( instancesDates[key].length > 1 ) {
				// several instances on the same day
				html += "<a href=\"javascript:void(0);\" class=\"multipleInstanceLabel inactive\" >" + monthLabel + ' ' + day + "</a>\n";
				html += "<div class=\"multipleInstance\">\n";				
				for ( var i = 0; i < instancesDates[key].length; i++ ) {
					var instance = instancesDates[key][i];
					var hour = instance['timestamp'].slice(8, 10) + ':' + instance['timestamp'].slice(10, 12) + ':' + instance['timestamp'].slice(12, 14);
					html += "<a href=\"" + instance['url'] + "\">" + monthLabel + ' ' + day + ', ' + hour + "</a>\n";
				}
				html += "</div>\n";
			} else {
				// one instance on one day
				html += "<a href=\"" + instance['url'] + "\">" + monthLabel + ' ' + day + "</a>\n";
			}
		  	html += "</div>\n";
		}
		html += "<div class=\"clearer\">&nbsp;</div>\n";
		html += "</div><!-- end .intancesList --> \n";
		html += "<div class=\"clearer\">&nbsp;</div>\n";
		
		html += "</div><!-- end #monthCalendar -->\n"
		html += "</div><!-- ending year -->\n";
		html += "</div><!-- end .scale -->\n";
	}
	html += "<div class=\"clearer\">&nbsp;</div>\n";
	html += "</div><!-- end .calendarSlider -->\n"; 
	return html;
}


/*
* createFolderListSnippet : creates the HTML snippet of the folder list (which contains folders and archive units)
* param : json table
*/
function createFolderListSnippet(json) {
	var html = "<div class=\"folderList\" id=\"folderList1\">\n";
	html += "<div class=\"label\">Folder list</div>\n";
	html += addFolderElts(json);
	html += "</div>\n";	
	return html;
}

/* 
* addFolderElts : recursive function which creates the HTML snippet of a folder or an archive unit in the folder list
*/
function addFolderElts(tab) {
	html = '';
	html += "<ul>\n";
	for ( var i = 0; i < tab.length; i++ ) {
		html += "<li class=\"" + tab[i]['type'] + "\">\n";
		
		// label
		if ( tab[i]['icon'] != undefined ) {
			// archive units can have icon
			html += "<span class=\"label\" style=\"background-image: url('" + tab[i]['icon'] + "')\">";
		} else {
			html += "<span class=\"label\">";
		}
		
		// archive name
		html += "<span class=\"name\">"
		if ( tab[i]['url'] != undefined ) {
			html += "<a href=\"" + tab[i]['url'] + "\" target=\"_top\">" + tab[i]['label'] + "</a>";
		} else {
			html += tab[i]['label'];
		}
		html += "</span>";
		
		// editor
		if ( (tab[i]['configUrl'] != undefined) || (tab[i]['description'] != undefined) || (tab[i]['creator'] != undefined) || (tab[i]['timestamp'] != undefined) ) {
			html += "<span class=\"editor\">";
			if ( tab[i]['configUrl'] != undefined ) {
				// "configuration" : access the page where the folder or the archive can be configured
				html += "<a class=\"config icon\" title=\"configuration\" href=\"" + tab[i]['configUrl'] + "\" target=\"_top\">";
				html += "<span class=\"accessibility\">Configuration</span>";
				html += "</a>";
			}
			if ( (tab[i]['description'] != undefined) || (tab[i]['creator'] != undefined) || (tab[i]['timestamp'] != undefined) ) {
				// "informations" : display more informations about the archive
				html += "<a class=\"infos icon\" title=\"informations\" href=\"javascript:void(0);\">";
				html += "<span class=\"accessibility\">Informations</span>";
				html += "</a>";
			}
			html += "</span><!-- end .editor-->\n";
			html += "</span><!-- end .label -->\n";
		
			// detail of the archive
			if ( (tab[i]['description'] != undefined) || (tab[i]['creator'] != undefined) || (tab[i]['timestamp'] != undefined) ) {
				html += "<div class=\"detail\">\n";
				if ( tab[i]['creator'] != undefined || tab[i]['timestamp'] != undefined ) {
					// creation of the archive or floder
					html += "<div class=\"creation\">Created";
					if ( tab[i]['creator'] != undefined ) {
						// creator
						html += " by " + tab[i]['creator'];
					}
					if ( tab[i]['timestamp'] != undefined ) {
						// timestamp of the creation
						html += " at " + tab[i]['timestamp'];
					}
					html += "</div>\n";
				}
				if ( tab[i]['description'] != undefined ) {
					// description of the archive
					html += "<div class=\"description\">" + tab[i]['description'] + "</div>\n";
				}
				html += "</div>\n";
			}
		}
		if ( tab[i]['children'] != undefined ) {
			// the foler have children folders or archives
			html += addFolderElts(tab[i]['children']);
		}
		html += "</li>\n";
	}
	html += "</ul>\n";
	return html;
}
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


$(window).ready(function() {

	// initialize the iframe height
	resetFrame();
	// update the iframe height on window resize
	window.onresize = resetFrame;
	

	/** time browser **/
	
	// display the instances on the calendar
	positionCalendarInstances();
	
	// resize calendars on click
	$(".year > .label").click(function () {
		$(this).parent('.year').parent('.scale').toggleClass("bigScale smallScale", 0);
		resetFrame();
		setCalendarSliderWidth();
	});
		
	// multiple instances
	$('.multipleInstanceLabel').click(function() {
		$(this).nextAll('.multipleInstance').toggle();
		$(this).toggleClass('active inactive');
	});
	
	
	/** organizer **/
	
	// drawers
	// close all the drawers when page is loaded
	$(".toolsDrawer").each(function() {
		$(this).addClass('closed');
	});
	// open / close the drawers on click
	$(".toolsDrawer > div.label").click(function () {
		$(this).parent('.toolsDrawer').toggleClass("closed opened");
		// the active drawer is above the others
		$('.toolsDrawer').css('z-index', 1000);
		$(this).parent('.toolsDrawer').css('z-index', 1100);
		
		resetFrame();
	});	
	// click on time browser drawer
	$(".timeBrowser.toolsDrawer > div.label").click(function () {
		var parentId = $(this).parent().attr('id');
    	$("#"+parentId+" .toolsDrawerContent").toggle('fast');
    	if ( $(this).parent('.toolsDrawer').is('.closed') ) {
			// when drawer is closed, resize all the calendars to small size
			$('.scale').removeClass("bigScale");
			$('.scale').addClass("smallScale");
		}
		setCalendarSliderWidth();
	});	
	// click on organizer drawer
	$(".organizer.toolsDrawer > div.label").click(function () {
		var parentId = $(this).parent().attr('id');
    	slideElement($("#"+parentId+" .toolsDrawerContent"), 'fast');
		if ( $(this).parent('.toolsDrawer').is('.closed') ) {
			// when drawer is closed, close all the inside elements
			closeFolderDrawers();
		}
	});
	
	// open / close folders in organizer
	closeFolderDrawers();
	$(".folder > .label > .name").click(function() {
		var parentLi = $(this).parent('.label').parent('li');
		$(parentLi).toggleClass("closed opened");
		slideElement($(parentLi).children('ul'), 400);
	});
	
	// open the time browser when the navigation link "all" is cicked
	$('#navigation .seeAll').click(function(evt) {
		// there is no time browser
		if ( $('.timeBrowser').length == 0 ) {
			return;
		}
		// there is a time browser => open / close it instead of activate the link
		evt.preventDefault();
		$('#timeBrowser1Content').toggle('fast');
		$('#timeBrowser1').toggleClass("closed opened");
		if ( $('#timeBrowser1').is('.closed') ) {
			// when drawer is closed, resize all the calendars to small size
			$('.scale').removeClass("bigScale");
			$('.scale').addClass("smallScale");
		}
		// the active drawer is above the others
		$('.toolsDrawer').css('z-index', 1000);
		$('#timeBrowser1').css('z-index', 1100);
		setCalendarSliderWidth();
	});
	
	// display / hide the editor elements in organizer
	$('#tools #organizer1Content li > .label > .editor').css('visibility', 'hidden');
	var hoverIntentConfig = {    
		over: function() {
			$(this).children('.editor').css('visibility', 'visible');
		},
		out: function() {
			$(this).children('.editor').css('visibility', 'hidden');
		}, 
		timeout: 700
	};
	$('#tools li:has(.editor) > .label').hoverIntent(hoverIntentConfig);

	// display / add the detail of the archives
	$('.detail').css('display', 'none');	
	$('.editor .infos').click(function() {
		$(this).parent('.editor').parent('.label').parent('li').children('.detail').toggle();
	});
	
});


/** time browser **/

function positionCalendarInstances() {
	// proportional vertical calendar
	$(".timeBrowser.verticalDrawers.proportional .instance").each(function(i, inst) {
		var instId = inst.id;
		instId = instId.replace('instance', '');
		var month = instId[4] + instId[5];
		var day = instId[6] + instId[7];
		$(inst).css('top', day+'em');
		$(inst).css('left', (3.25*month)+'em');
	});
	
	// non proportional vertical calendar
	$(".timeBrowser.verticalDrawers.nonproportional .scale").each(function(i, scale) {
		// initialise month array
		var monthArray = new Array();
		for ( var i = 0; i <= 12; i++ ) {
			monthArray[i] = 0;
		}	
		$(scale).find(".instance").each(function(i, inst) {
			var instId = inst.id;
			instId = instId.replace('instance', '');
			var month = parseInt(instId[4] + instId[5], 10);
			monthArray[month] += 1;
			var day = instId[6] + instId[7];
			$(inst).css('top', monthArray[month]+'em');
			$(inst).css('left', (3.25*month)+'em');
		});		
		var maxHeight = 0;
		for (var i=1; i < monthArray.length; i++) {
			if (monthArray[i] > maxHeight) {
				maxHeight = monthArray[i];
			}
		}
		$(scale).find('.month').css('height', maxHeight+'em');
	});
}

function setCalendarSliderWidth() {
	var totalWidth = 0;
	$('.year').each(function(index, elt) {
		totalWidth += $(elt).width();
		totalWidth += parseFloat($(elt).css('margin-right'));
		totalWidth += 15;
	});
	totalWidth += parseFloat($('.year:first').css('margin-left'));
	$('.calendarSlider').css('width', totalWidth + 'px');
}


/** organizer **/

function closeFolderDrawers() {
	$(".folder").each(function() {
		$(this).children('ul').css('display', 'none');
		$(this).removeClass('opened');
		$(this).addClass('closed');
	});
}

// slide up or down an element (vertical toggle)
function slideElement(elt, duration) {
	if ( elt.css('display') == 'none') {
    	elt.slideDown(duration, resetFrame);
    } else {
    	elt.slideUp(duration, resetFrame);
    }
}


/** iframe **/

// reset iframe dimensions OK for IE too
function resetFrame() {
	if ( $("#mainFrame").length > 0 ) {
		hauteurBody = document.documentElement.clientHeight;
		mainFrameHeight = hauteurBody;
		metaBrowserHeight = document.getElementById("metabrowser").clientHeight;
		// -10 : small arbitrary margin
		heightTarget = (mainFrameHeight - metaBrowserHeight) - 10;
		document.getElementById("mainFrame").style.height = heightTarget + "px";
	}
}

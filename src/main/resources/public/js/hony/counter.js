var hony_counter = 0;
window.onload = function(e){
	var $counter = $("#counter");
	var path = $counter.data("path");
	
	var t = setInterval(function(){
		refreshCounter($counter, path);
	}, 5000);
}

function refreshCounter($counter, path) {
	$.getJSON('https://www.honybot.com/counters/'+path+'/get', function(data) {
	    $counter.text(data.value);
	});
}
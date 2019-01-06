
var sc2league = "";
var forEach = function (array, callback, scope) {
	for (var i = 0; i < array.length; i++) {
		callback.call(scope, i, array[i]);
	}
};
window.onload = function(){
	var max = 2160;
	sc2league = $("#league-tier").data("league");
	var path = $("#league-tier").data("path");
	forEach(document.querySelectorAll('.progress'), function (index, value) {
		percent = value.getAttribute('data-progress');
		value.querySelector('.fill').setAttribute('style', 'stroke-dashoffset: ' + ((100 - percent) / 100) * max);
	});
	var t = setInterval(function(){
		setProgress(path);
	}, 300000);
	
}

function setProgress(path){
	var max = 2160;
	forEach(document.querySelectorAll('.progress'), function (index, value) {
		
		$.getJSON('https://www.honybot.com/mmr/'+path+'/get', function(data) {
			//reload page if league changes
			if(data.league != sc2league){
				location.reload();
			}		
			//set data-progress
			$(".progress").data("progress", data.progress);
			//draw stroke
			value.querySelector('.fill').setAttribute('style', 'stroke-dashoffset: ' + ((100 - data.progress) / 100) * max);
			//set league_tier
			$("#league-tier").text(data.league_tier);
			//set mmr/upper
			$("#mmr").text(data.mmr + "/" + data.upper);
		});
	});
}

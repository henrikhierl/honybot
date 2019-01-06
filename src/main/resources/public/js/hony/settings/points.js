$(document).ready(function() {
	$("#settings").submit(function(event) {
		event.preventDefault();
		var formdata = $(this).serializeArray();
		console.log(formdata);
		var data = {};
		$.each(formdata, function(i, v) {
			data[v.name] = v.value;
		});
		if(!data["points-check"]){
			data["points-check"] = "off";
		}
		if(!data["points-whisper"]){
			data["points-whisper"] = "off";
		}
		$.ajax("/panel/points", {
			data: data,
			dataType: "json",
			method: "POST",
			success: function(data, textStatus, jqXHR){
				new PNotify({
                	title: data.title,
                	text: data.text,
                	type: data.type,
                	styling: 'bootstrap3',
            	});
			},
			error: function(){
				console.log("error");
			}
		});
	});
});
$(document).ready(function() {
	$("#settings").submit(function(event) {
		event.preventDefault();
		var formdata = $(this).serializeArray();

		var data = {};
		$.each(formdata, function(i, v) {
			data[v.name] = v.value;
		});
		if(!data["vip-check"]){
			data["vip-check"] = "off";
		}
		if(!data["vip-stacking"]){
			data["vip-stacking"] = "off";
		}
		if(!data["vip-check-extension"]){
			data["vip-check-extension"] = "off";
		}
		$.ajax("/panel/vip", {
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
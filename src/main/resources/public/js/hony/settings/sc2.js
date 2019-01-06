$(document).ready(function() {
	$("#settings").submit(function(event) {
		event.preventDefault();
		var formdata = $(this).serializeArray();
		
		var data = {};
		$.each(formdata, function(i, v) {
			data[v.name] = v.value;
		});
		
		if(!data["sc2-check"]){
			data["sc2-check"] = "off";
		}
		if(!data["sc2-title-check"]){
			data["sc2-title-check"] = "off";
		}
		if(!data["sc2-show-games"]){
			data["sc2-show-games"] = "off";
		}
		$.ajax("/panel/sc2", {
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
	
	
	$("#test").click(function(event) {
		event.preventDefault();
		var formdata = $("#settings").serializeArray();
		
		var data = {};
		$.each(formdata, function(i, v) {
			data[v.name] = v.value;
		});
		console.log(data);
		
		$.ajax("/panel/sc2/test", {
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
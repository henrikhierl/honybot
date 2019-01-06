$(document).ready(function() {
	$("#settings").submit(function(event) {
		event.preventDefault();
		var formdata = $(this).serializeArray();
		
		var data = {};
		$.each(formdata, function(i, v) {
			data[v.name] = v.value;
		});
		if(!data["betting-check"]){
			data["betting-check"] = "off";
		}
		if(!data["betting-mmr-quotas-check"]){
			data["betting-mmr-quotas-check"] = "off";
		}
		
		$.ajax("/panel/betting", {
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
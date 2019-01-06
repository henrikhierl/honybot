$(document).ready(function() {	
	$('.control-button').click(function(event){
		//show loading circle
		event.preventDefault();    
		var data = $(this).closest('form').serialize();	
		if(data == "type=bot-start"){
			$("#start-button").removeClass("fa-play").addClass("fa-spinner fa-spin");
		}
		if(data == "type=bot-restart"){
			$("#restart-button").addClass("fa-spin");
		}
    	var jqxhr = $.post("/panel/control_center", data, function(response){
    		console.log(response);
    		if(data == "type=bot-start"){
    			$("#start-button").removeClass("fa-spinner fa-spin").addClass("fa-play");
    		}
    		if(data == "type=bot-restart"){
    			$("#restart-button").removeClass("fa-spin");
    		}
    		if(response.type == "success"){
    			var label = $("#bot-status");
    			if(data == "type=bot-start"){
    				label.removeClass().addClass("label label-success");
    				label.text("bot started");
    			}else if(data == "type=bot-stop"){
    				label.removeClass().addClass("label label-danger");
    				label.text("bot stopped");
    				
    			}				
    		}
    		new PNotify({
            	title: response.title,
            	text: response.text,
            	type: response.type,
            	styling: 'bootstrap3',
        	});
    	
    	})
    	  .fail(function(responseText) {
    	    console.log(responseText.type)
    		  alert( "error" );
    	  });    		
	});
	
	$('form').submit(function(event){
		//show loading circle
		event.preventDefault();    		
    	var jqxhr = $.post("/panel/control_center", $(this).serialize(), function(response){
    		new PNotify({
            	title: response.title,
            	text: response.text,
            	type: response.type,
            	styling: 'bootstrap3',
        	});
    	
    	})
    	  .fail(function(responseText) {
    	    console.log(responseText.type)
    		  alert( "error" );
    	  });    		
	});
	
	$("#community").change(function(event){
		var value = $(this).prop("checked");
		var data = {};
		data["type"] = "community";
		data["value"] = value;
		console.log(data);
		$.ajax("/panel/control_center", {
			"data": data,
			"method": "POST",
			"dataType": "json",
			"success": function(response){
				new PNotify({
	            	title: response.title,
	            	text: response.text,
	            	type: response.type,
	            	styling: 'bootstrap3',
	        	});
			}
		});
	});
	
	
});
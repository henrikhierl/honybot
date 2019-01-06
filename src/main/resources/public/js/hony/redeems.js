$(document).ready(function(){
	
	console.log("loading scripts for redeems page");
	
	var sendDelete = function(paramsString, form_element) {
		// send post
		var url = "/panel/redeems";
		console.log(paramsString);
		
		//set trashcan to spinning logo
		console.log($("button", form_element));
		var button = $("button", form_element);
		button.html("<i class=\"fa fa-spinner fa-spin icon-large\"></i>");
		
		var jqxhr = $.post(url, paramsString, function(response){
			response = JSON.parse(response);
			console.log(response);
			if(response.type === "success") {
				// delete closest table row
				form_element.closest("tr").remove();
				// if no row left, remove table
				var table_rows = $("#redeems-table tbody tr");
				if(table_rows.size() === 0) {
					$("#redeems-block").hide();
					// TODO: show no open redeems
					$("#no-redeems-block").show();
				}
			}else{
				button.html("<i class=\"fa fa-trash icon-large\"></i>");
			}
    		new PNotify({
            	title: response.title,
            	text: response.text,
            	type: response.type,
            	styling: 'bootstrap3',
        	});
    	
    	}).fail(function(responseText) {
    	    console.log(responseText.type)
    		  alert( "error" );
    	});
    	
	};
	
	// bind delete on submit
	$(".delete-redeem-form").submit(function(event){
		event.preventDefault();
		console.log("submit");
		console.log( $( this ).serialize() );
		sendDelete($( this ).serialize(), $(this));
	});
	

});
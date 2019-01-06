$(document).ready(function(){
	console.log("loading scripts for rewards page");
	
	var sendDelete = function(paramsString, form_element) {
		// send post
		var url = "/panel/rewards";
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
				var table_rows = $("#rewards-table tbody tr");
				if(table_rows.size() === 0) {
					$("#rewards-block").hide();
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
	

	var sendAdd = function(paramsString, form_element) {
		// send post
		var url = "/panel/rewards";
		console.log(paramsString);
		
		//set button to spinning logo
		console.log($("button", form_element));
		var button = $("button", form_element);
		button.html("<i class=\"fa fa-spinner fa-spin icon-large\"></i>");
		
		var jqxhr = $.post(url, paramsString, function(response){
			response = JSON.parse(response);
			console.log(response);
			button.html("Create");
			
    		new PNotify({
            	title: response.title,
            	text: response.text,
            	type: response.type,
            	styling: 'bootstrap3',
        	});
			
			if(response.type !== "success"){
				return;
			}
			
			// add row to table
			var new_row = getRowFromForm(response.data);
			
			$(".delete-reward-form", new_row).submit(function(event){
				event.preventDefault();
				console.log("submit");
				console.log( $( this ).serialize() );
				sendDelete($( this ).serialize(), $(this));
			});
			
			// clear form
			$("#add-reward-form")[0].reset();
			
			$("#rewards-table").append(new_row);
			//show the table when adding a reward
			$("#rewards-block").show();
			
			//TODO: add new modal
			/*
			var new_modal = createModalByRewardId(response.data);
			$("#rewards-modals").append();
			*/
    	
    	}).fail(function(responseText) {
    	    console.log(responseText.type)
    		  alert( "error" );
    	});
    	
	};
	
	var sendUpdate = function(paramsString, form_element) {
		var url = "/panel/rewards";
		console.log(paramsString);
		
		//set update button to spinning logo
		console.log($("button", form_element));
		var button = $("button", form_element);
		button.html("<i class=\"fa fa-spinner fa-spin icon-large\"></i>");
		
		
		var jqxhr = $.post(url, paramsString, function(response){
			response = JSON.parse(response);
			console.log(response);
			button.html("Update");
			
    		new PNotify({
            	title: response.title,
            	text: response.text,
            	type: response.type,
            	styling: 'bootstrap3',
        	});
			
			if(response.type !== "success"){
				return;
			}
			
			// update corresponding row
			// get reward id
			console.log("before:", form_element);
			var reward_id = $("input[name=ID]", form_element).val();
			// get row by reward id
			var row = $("#reward-"+reward_id);
			console.log("deleted row", row);
			var new_row = getRowFromUpdateForm(form_element, reward_id);
			console.log("after:", form_element);
			console.log("replacement row", new_row);
			row.replaceWith(new_row);
			
    	
    	}).fail(function(responseText) {
    	    console.log(responseText.type)
    		  alert( "error" );
    	});
	}
	
	var getRowFromForm = function(id){
		
		var image_url = $("#image_url").val();
		var title = $("#title").val();
		var command = $("#command").val();
		var response = $("#response").val();
		var description = $("#description").val();
		var cost = $("#cost").val();
		var permission = $("#permission").val();
		var vip = $("#vip").val();
		console.log(title, command, response, description, cost, permission, vip);

		table_row = $("<tr id=\"reward-"+id+"\"></tr>");
		if(image_url !== null && image_url !== ""){
			table_row.append("<td><img src=\""+image_url+"\" width=\"50\" height=\"50\"/></td>");
		}else{
			table_row.append("<td><img src=\"https://upload.wikimedia.org/wikipedia/commons/c/c1/Human-emblem-star-black-128.png\" width=\"50\" height=\"50\" /></td>");
		}
		
		table_row.append("<td>"+title+"</td>");
		table_row.append("<td>"+command+"</td>");
		table_row.append("<td>"+response+"</td>");
		table_row.append("<td>"+description+"</td>");
		table_row.append("<td>"+cost+"</td>");
		table_row.append("<td>"+vip+"</td>");
		table_row.append("<td>"+permission+"</td>");
		table_row.append("<td><button class=\"btn btn-primary\" data-toggle=\"modal\" data-target=\"#modal-popout-"+id+"\" type=\"button\" disabled><i class=\"fa fa-wrench icon-large\"></i></button></td>");
		table_row.append("<td><form action=\"\" method=\"POST\" class=\"delete-reward-form\" >" +
				"<input type=\"hidden\" name=\"action\" value=\"remove\"> " +
				"<input type=\"hidden\" name=\"ID\" value=\""+id+"\"> "+
			"<button class=\"btn btn-danger\"><i class=\"fa fa-trash icon-large\"></i></button> " +
			"</form></td>");
		
		return table_row;	
	}
	
	var createModalByRewardId = function(reward_id){
		
	}
	
	var getRowFromUpdateForm = function(form_element, id){
		
		console.log("id input", $("input[name=ID]", form_element));
		var image_url = $("input[name=image_url]", form_element).val();
		var title = $("#title-"+id).val();
		var command = $("#command-"+id, form_element).val();
		var response = $("#response-"+id, form_element).val();
		var description = $("#description-"+id, form_element).val();
		var cost = $("#cost-"+id, form_element).val();
		var permission = $("#permission-"+id, form_element).val();
		var vip = $("#vip-"+id, form_element).val();
		console.log("values: ",title, command, response, description, cost, permission, vip);
		
		table_row = $("<tr id=\"reward-"+id+"\"></tr>");
		if(image_url !== null && image_url !== ""){
			table_row.append("<td><img src=\""+image_url+"\" width=\"50\" height=\"50\"/></td>");
		}else{
			table_row.append("<td><img src=\"https://upload.wikimedia.org/wikipedia/commons/c/c1/Human-emblem-star-black-128.png\" width=\"50\" height=\"50\" /></td>");
		}
		
		table_row.append("<td>"+title+"</td>");
		table_row.append("<td>"+command+"</td>");
		table_row.append("<td>"+response+"</td>");
		table_row.append("<td>"+description+"</td>");
		table_row.append("<td>"+cost+"</td>");
		table_row.append("<td>"+vip+"</td>");
		table_row.append("<td>"+permission+"</td>");
		table_row.append("<td><button class=\"btn btn-primary\" data-toggle=\"modal\" data-target=\"#modal-popout-"+id+"\" type=\"button\"><i class=\"fa fa-wrench icon-large\"></i></button></td>");
		table_row.append("<td><form action=\"\" method=\"POST\" class=\"delete-reward-form\" >" +
				"<input type=\"hidden\" name=\"action\" value=\"remove\"> " +
				"<input type=\"hidden\" name=\"ID\" value=\""+id+"\"> "+
			"<button class=\"btn btn-danger\"><i class=\"fa fa-trash icon-large\"></i></button> " +
			"</form></td>");
		
		return table_row;
		
	}
	
	// bind delete on submit
	$(".delete-reward-form").submit(function(event){
		event.preventDefault();
		console.log("submit");
		console.log( $( this ).serialize() );
		sendDelete($( this ).serialize(), $(this));
	});
	
	// bind add on submit
	$("#add-reward-form").submit(function(event){
		event.preventDefault();
		console.log("submit");
		console.log( $( this ).serialize() );
		sendAdd($( this ).serialize(), $(this));
	});
	
	//bind update on submit
	$(".update-reward-form").submit(function(event){
		event.preventDefault();
		console.log("submit update");
		console.log( $( this ).serialize() );
		sendUpdate($( this ).serialize(), $(this));
	});
	
});
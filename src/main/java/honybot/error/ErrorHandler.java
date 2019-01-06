package honybot.error;

import java.util.ArrayList;
import java.util.List;

public class ErrorHandler {
	private List<Error> errors;
	
	public ErrorHandler(){
		errors = new ArrayList<>();
	};
	
	public boolean add(Error err){
		return errors.add(err);
	}
	
	public void clear(){
		errors.clear();
	}
	
	
}

package event;

public class InvalidListSizeException extends Exception {
	   public InvalidListSizeException (){
		   super("The list must contain exactly two entries");
		   }
}


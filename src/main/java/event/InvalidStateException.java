package event;

public class InvalidStateException extends Exception {
	   public InvalidStateException (){
		   super("Entries cannot have same state!");
		   }

}

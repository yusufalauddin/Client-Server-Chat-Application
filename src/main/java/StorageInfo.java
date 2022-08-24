import java.io.Serializable;
import java.util.ArrayList;

public class StorageInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String clientName;
	String message;
	
	ArrayList<String> names; // who CAN receive the message
	ArrayList<Integer> msgRecipients; // who IS receiving the message
	
	boolean isUpdate; // true if updating client list, false otherwise
	
	StorageInfo() {
		names = new ArrayList<String>();
		msgRecipients = new ArrayList<Integer>();
		
		isUpdate = false;
		
		names.add("@everyone");
	}


}

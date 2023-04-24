package menuordertables;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Client {


	    private SimpleIntegerProperty id;
	    private SimpleStringProperty firstName;
	    private SimpleStringProperty lastName;
	    private SimpleStringProperty cardNum;

	    public Client(int id, String cardNum, String firstName, String lastName) {
	    	this.id = new SimpleIntegerProperty(id);
	        this.firstName = new SimpleStringProperty(firstName);
	        this.lastName = new SimpleStringProperty(lastName);
	        this.cardNum = new SimpleStringProperty(cardNum);

	    }

	    public int getId(){ return id.get();}
	    public void setId(int value){ id.set(value);}

	    public String getFirstName(){ return firstName.get();}
	    public void setFirstName(String value){ firstName.set(value);}

	    public String getLastName(){ return lastName.get();}
	    public void setLastName(String value){ lastName.set(value);}


	    public String getCardNum(){ return cardNum.get();}
	    public void setCardNum(String value){ cardNum.set(value);}

	    @Override
	    public String toString(){
	    	return id + ", " + firstName + ", " + lastName + ", " + cardNum;
	    }
}

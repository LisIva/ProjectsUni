package menuordertables;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Menu {

	    private SimpleIntegerProperty id;
	    private SimpleStringProperty name;
	    private SimpleIntegerProperty price;
	    private SimpleDoubleProperty discount;


	    public Menu(int id, String name, int price, double discount){
	    	this.id = new SimpleIntegerProperty(id);
	        this.name = new SimpleStringProperty(name);
	        this.price = new SimpleIntegerProperty(price);
	        this.discount = new SimpleDoubleProperty(discount);
	    }

	    public int getId(){ return id.get();}
	    public void setId(int value){ id.set(value);}

	    public String getName(){ return name.get();}
	    public void setName(String value){ name.set(value);}

	    public int getPrice(){ return price.get();}
	    public void setPrice(int value){ price.set(value);}

	    public double getDiscount(){ return discount.get();}
	    public void setDiscount(double value){ discount.set(value);}

	    @Override
	    public String toString(){
	    	return id + ", " + name + ", " + price + ", " + discount;
	    }
}

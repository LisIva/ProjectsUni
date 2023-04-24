package application;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import menuordertables.*;
import connection.ReadData;

public class OrderWindow {

	 public static ObservableList<Menu> order;
	 public static Label lbOrderT = new Label();
	 public static Label lbTableN = new Label();

	 private String style =  "-fx-border-color: #EE9223; "
	      		+ "-fx-effect: dropshadow(gaussian, #EC791C, 10, 0, 0, 0); "
	      		+ "-fx-focus-color: transparent;"
	      		+ "-fx-faint-focus-color: #EC791C22;";

	 private ImageView intView;
	 private VBox orderTypeBox, tableNumBox;
	 private TableView<Menu> menuView, orderView;
	 private Button addBtn, delBtn, ordBtn;
	 private ComboBox<String> orderTypeCBox;
	 private ComboBox<Integer> tableNumCBox;
	 private Stage primaryStage = new Stage();


	 public OrderWindow (){
		 order = FXCollections.observableArrayList();

		setImage();
		selectOrderType();  //выбор типа заказа
		selectTable();      //выбор номера доступного столика
		createMenu();
		createOrder();
		createButtons();
		setOrderBtnVisibility();
		selectDishes();
		nextWindow();

		Group root = new Group();
		root.getChildren().addAll(intView, orderTypeBox, tableNumBox, menuView, orderView, addBtn, delBtn, ordBtn);
		Scene scene = new Scene(root);
		primaryStage.setTitle("Restaurant");
		primaryStage.setScene(scene);
		primaryStage.show();

	 }

	 private void nextWindow() { //переход к новому окну
	        ordBtn.setOnAction((ActionEvent event) -> {
	     	primaryStage.close();
	     	PayWindow pay = new PayWindow();
	     });
}

	 ////////////////////////////////// вспомогательные методы класса //////////////////////////////
	 private boolean ordBtnVisibility() {

		 return order.size() > 0 && (lbOrderT.getText().equals("в ресторане")
					&& !lbTableN.getText().equals("") || order.size() > 0 &&lbOrderT.getText().equals("с собой"));

	 }

	 public static Text setText(String name, int fontWeight) {

	        Text text = new Text(name);
	        text.setFont(Font.font("Arial", FontWeight.NORMAL, fontWeight));
	        text.setFill(Color.rgb(67,53,66));
	        return text;
	}

	 /////////////////////////////////// методы скрытия кнопок и обработки событий //////////////////////////////

	 private void selectDishes() {
		    TableView.TableViewSelectionModel<Menu> selectionModelMenu = menuView.getSelectionModel();
		     TableView.TableViewSelectionModel<Menu> selectionModelOrder = orderView.getSelectionModel();

		     selectionModelMenu.selectedItemProperty().addListener( new ChangeListener<Menu>(){

		         public void changed(ObservableValue<? extends Menu> val, Menu oldVal, Menu newVal){

						addBtn.setOnAction((ActionEvent event) -> {
							if (newVal != null) {

								order.add(newVal);
								orderView.setItems(order);

								if (ordBtnVisibility())
										ordBtn.setVisible(true);
							}

						});
		         }

		     });


		     selectionModelOrder.selectedItemProperty().addListener( new ChangeListener<Menu>(){

		         public void changed(ObservableValue<? extends Menu> val, Menu oldVal, Menu newVal){

						delBtn.setOnAction((ActionEvent event) -> {
							if (newVal != null) {

								TablePosition cell = orderView.getSelectionModel().getSelectedCells().get(0);
								order.remove(cell.getRow());
								orderView.setItems(order);

								if (order.size() == 0)
									ordBtn.setVisible(false);
							}

						});
		         }

		     });
	 }

	 private void setOrderBtnVisibility() {
	     orderTypeCBox.setOnAction((ActionEvent event) -> {
			lbOrderT.setText(orderTypeCBox.getValue());
			if(lbOrderT.getText().equals("в ресторане"))
				tableNumBox.setVisible(true);
			else
				tableNumBox.setVisible(false);

			if (ordBtnVisibility())
					ordBtn.setVisible(true);

		});

		tableNumCBox.setOnAction((ActionEvent event) -> {
			lbTableN.setText(String.format("%d", tableNumCBox.getValue()));
			if (ordBtnVisibility())
					ordBtn.setVisible(true);
		});
     }

	 /////////////////////////////////////// методы создания элементов окошка ///////////////////////////////////////
	 private void createButtons() {

		addBtn = new Button("->");
		addBtn.setLayoutX(280);
		addBtn.setLayoutY(360);
		addBtn.setPrefWidth(40);
		addBtn.setStyle(style);

		delBtn = new Button("<-");
		delBtn.setLayoutX(280);
		delBtn.setLayoutY(440);
		delBtn.setPrefWidth(40);
		delBtn.setStyle(style);

		ordBtn = new Button("К оплате");
		ordBtn.setLayoutX(350);
		ordBtn.setLayoutY(190);
		ordBtn.setPrefWidth(80);
		ordBtn.setPrefHeight(60);
		ordBtn.setStyle(style);
		ordBtn.setVisible(false);
		ordBtn.setStyle(style);
	 }

	 private void createOrder() {
		 TableColumn<Menu, String> oCol1 = new TableColumn<>(" Блюдо ");
	     oCol1.setCellValueFactory(new PropertyValueFactory<Menu, String>("name"));

	     TableColumn<Menu, String> oCol2 = new TableColumn<>(" Цена ");
	     oCol2.setCellValueFactory(new PropertyValueFactory<Menu, String>("price"));


	     orderView = new TableView<>(order);
	     orderView.getColumns().addAll(oCol1, oCol2);
	     orderView.setPrefWidth(139);
	     orderView.setPrefHeight(150);
	     orderView.setLayoutX(340);
	     orderView.setLayoutY(340);
	     orderView.setStyle(style);
	 }

	 private void createMenu() {
		 ObservableList<Menu> menu = ReadData.menu; /* FXCollections.observableArrayList(

		            new Menu("Dish1", 1004, 12.2),
		            new Menu("Dish2", 2034, 13.2),
		            new Menu("Dish3", 6784, 18.2),
		            new Menu("Dish4", 5674, 6.2)
		        );*/


	     TableColumn<Menu, String> col1 = new TableColumn<>(" Блюдо ");
	     col1.setCellValueFactory(new PropertyValueFactory<Menu, String>("name"));

	     TableColumn<Menu, String> col2 = new TableColumn<>(" Цена ");
	     col2.setCellValueFactory(new PropertyValueFactory<Menu, String>("price"));

	     TableColumn<Menu, String> col3 = new TableColumn<>(" Скидка ");
	     col3.setCellValueFactory(new PropertyValueFactory<Menu, String>("discount"));



	     menuView = new TableView<>(menu);
	     menuView.getColumns().addAll(col1, col2, col3);
	     menuView.setPrefWidth(198);
	     menuView.setPrefHeight(150);
	     menuView.setLayoutX(60);
	     menuView.setLayoutY(340);
	     menuView.setStyle(style);
	 }

	 private void selectTable() {
		    ObservableList<Integer> tableNumbers = ReadData.tables;//FXCollections.observableArrayList("14", "16", "22", "26");
			tableNumCBox = new ComboBox<Integer>(tableNumbers);
			//tableNumCBox.setValue(" ");
			tableNumCBox.setPrefWidth(70);
			tableNumCBox.setPrefHeight(10);

			tableNumCBox.setStyle(style);

			Text textTN = setText("Выберете №столика:", 13);
			Text textTNS = setText(" ", 2);

			tableNumBox = new VBox();
			tableNumBox.getChildren().addAll(textTN, textTNS, tableNumCBox);
			tableNumBox.setLayoutX(90);
			tableNumBox.setLayoutY(235);
			tableNumBox.setAlignment(Pos.CENTER);
			tableNumBox.setVisible(false);
	 }


	 private void selectOrderType() {

		    ObservableList<String> orderType = FXCollections.observableArrayList("с собой", "в ресторане");
			orderTypeCBox = new ComboBox<String>(orderType);
			orderTypeCBox.setValue(" "); // выбранный элемент по умолчанию

			orderTypeCBox.setPrefWidth(120);
			orderTypeCBox.setPrefHeight(10);

			orderTypeCBox.setStyle(style);

			Text textOT = setText("Укажите тип заказа:", 13);
			Text textOTS = setText(" ", 2);

			orderTypeBox = new VBox();
			orderTypeBox.getChildren().addAll(textOT, textOTS, orderTypeCBox);
			orderTypeBox.setLayoutX(90);
			orderTypeBox.setLayoutY(180);
			orderTypeBox.setAlignment(Pos.CENTER);
	 }

	 private void setImage() {
		    Image intIMG = new Image(getClass().getResource("/Intface.JPG").toString());

			double koeffResize = 1.3;
			intView = new ImageView(intIMG);
			intView.setPreserveRatio(true);
			intView.setFitWidth(koeffResize * intIMG.getWidth());
	 }
}

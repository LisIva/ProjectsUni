package application;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import menuordertables.*;
import connection.ReadData;
import connection.WriteData;

public class PayWindow {


	private Button calculBtn = new Button("Посчитать");
	private Button payBtn = new Button("Оплатить");
	private TextArea loyalCard = new TextArea();
	private Stage primaryStage = new Stage();
	private TableView<Menu> orderView;

	private int index;
	private Text txtSum;
	private Text txtSumWD;
	private TextArea orderSumWD = new TextArea();
	private int midShift = -75;

	private String styleB =  "-fx-border-color: #715A4C; "
      		+ "-fx-effect: dropshadow(gaussian, #715A4C, 14, 0, 0, 0); "
      		+ "-fx-focus-color: transparent;"
      		+ "-fx-faint-focus-color: #715A4C30;";

	public static int id;
	public static TextArea orderSum = new TextArea();

	public PayWindow() {

		Image intIMG = new Image(getClass().getResource("/Intface2.jpg").toString());
		ImageView intView = new ImageView(intIMG);

		generateTxtArea(); // TextArea ввод карты и вывод общей суммы заказа
		concealSum();
		generateBtns(); // кнопки посчитать заказ, оплатить заказ
		createOrder();

		calculOrder(); // подсчет суммы
		payRequest();  // оплата заказа

		Group root = new Group();
		root.getChildren().addAll(intView, loyalCard, calculBtn, orderSum, payBtn, orderView, orderSumWD, txtSum, txtSumWD);
		Scene scene = new Scene(root);

        primaryStage.setTitle("Order pay");
        primaryStage.setScene(scene);
        primaryStage.show();

	}

	private void payRequest(){

		payBtn.setOnAction((ActionEvent event) -> {

			primaryStage.close();
			WriteData writer = new WriteData();
		});
	}

///////////////////////////////////////////// подсчет суммы заказа ////////////////////////////////
	private void calculOrder() {

		//System.out.println(orderSum.getText().equals(""));
		calculBtn.setOnAction((ActionEvent event) -> {

			orderSum.setText(String.format("%.2f", calcul()));
			orderSumWD.setText(String.format("%.2f", calculWD()));

			payBtn.setVisible(true);
		});
	}

	private double calcul() {
		double sum = 0;
		boolean discount = false;
		index = searchId();

		if (!loyalCard.getText().equals("") && validLoyalCard())
			discount = true;

		for (Menu menu : OrderWindow.order) {

			sum += menu.getPrice();
			if(discount)
				sum -= menu.getPrice()*menu.getDiscount()*0.01;
		}
		return sum;
	}

	private double calculWD() {
		double sum = 0;

		for (Menu menu : OrderWindow.order)
			sum += menu.getPrice();
		return sum;
	}

///////////////////////////////////////////// проверка карты ////////////////////////////////
	private boolean validLoyalCard () {

		if(index == -1)
			return false;
		else
			return isValidCard(index);

	}

	private int searchId() {

		int id = -1;
		for(int i = 0; i < ReadData.clients.size(); i++) {

			if( ReadData.clients.get(i).getFirstName().equals(IdentityWindow.fNameL.getText()) &&
				ReadData.clients.get(i).getLastName().equals(IdentityWindow.lNameL.getText())) {
				id = i;
				this.id = ReadData.clients.get(i).getId();
			}
		}

		return id;
	}

	private boolean isValidCard (int id) {

		System.out.println(ReadData.clients.get(id).getCardNum());
		System.out.println(ReadData.clients.get(id).getFirstName());
		return ReadData.clients.get(id).getCardNum().equals(loyalCard.getText());

	}

//////////////////////////////////////////////////////////список заказанных блюд //////////////////////////////////////////////
	private void createOrder() {

		ObservableList<Menu> order = OrderWindow.order;

		TableColumn<Menu, String> col1 = new TableColumn<>(" Блюдо ");
	     col1.setCellValueFactory(new PropertyValueFactory<Menu, String>("name"));

	     TableColumn<Menu, String> col2 = new TableColumn<>(" Цена ");
	     col2.setCellValueFactory(new PropertyValueFactory<Menu, String>("price"));



	     orderView = new TableView<>(order);
	     //col1.setPrefWidth(80);
	     col1.setMinWidth(80);
	     //col2.setPrefWidth(70);
	     col2.setMinWidth(70);
	     orderView.getColumns().addAll(col1, col2);
	     orderView.setPrefWidth(190);
	     orderView.setPrefHeight(140);
	     orderView.setLayoutX(195);
	     orderView.setLayoutY(365 + midShift);
	     orderView.setStyle(styleB);
	}

////////////////////////////////////скрытие лишней суммы заказа ///////////////////
	private void concealSum() {

		orderSum.textProperty().addListener(new ChangeListener<String>() {

			public void changed(ObservableValue<? extends String> val, String oldVal, String newVal) {

				if (newVal != null) {

					if (!orderSumWD.getText().equals("") && !orderSum.getText().equals(orderSumWD.getText())) {
						orderSum.setVisible(true);
						txtSum.setVisible(true);
					}
				}
			}

		});

		orderSumWD.textProperty().addListener(new ChangeListener<String>() {

			public void changed(ObservableValue<? extends String> val, String oldVal, String newVal) {

				if (newVal != null) {

					if (!orderSum.getText().equals("") && !orderSum.getText().equals(orderSumWD.getText())) {
						orderSum.setVisible(true);
						txtSum.setVisible(true);
					}
				}
			}

		});
	}



 ////////////////////////////////////////// задание кнопок ////////////////////////////////
	private void generateBtns() {

		calculBtn.setLayoutX(425);
		calculBtn.setLayoutY(350);
		calculBtn.setPrefWidth(85);
		calculBtn.setPrefHeight(50);
		calculBtn.setStyle(styleB);

		payBtn.setLayoutX(425);
		payBtn.setLayoutY(270);
		payBtn.setPrefWidth(85);
		payBtn.setPrefHeight(50);
		payBtn.setStyle(styleB);
		payBtn.setVisible(false);
	}

	private void generateTxtArea() {

		int leftShift = 35;
		loyalCard.setPromptText("№ карты клиента (если есть)");
		loyalCard.setFocusTraversable(false);

		loyalCard.setPrefHeight(40);
		loyalCard.setPrefWidth(200);

		loyalCard.setLayoutX(190);
		loyalCard.setLayoutY(300 + midShift);
		loyalCard.setStyle("-fx-border-color: #715A4C; "
	      		+ "-fx-effect: dropshadow(gaussian, #715A4C, 30, 0, 0, 0); "
	      		+ "-fx-focus-color: transparent;"
	      		+ "-fx-faint-focus-color: #715A4C30;");

		orderSum.setPrefHeight(45);
		orderSum.setPrefWidth(90);
		orderSum.setLayoutX(70);
		orderSum.setLayoutY(250 + leftShift);
		orderSum.setStyle(styleB);
		orderSum.setPromptText("Заказ на сумму:");
		orderSum.setFocusTraversable(false);
		orderSum.setEditable(false);
		orderSum.setVisible(false);

		orderSumWD.setPrefHeight(45);
		orderSumWD.setPrefWidth(90);
		orderSumWD.setLayoutX(70);
		orderSumWD.setLayoutY(330 + leftShift);
		orderSumWD.setStyle(styleB);
		orderSumWD.setPromptText("Заказ на сумму:");
		orderSumWD.setFocusTraversable(false);
		orderSumWD.setEditable(false);

		txtSum = setText("C учетом скидки:", 12);
		txtSum.setLayoutX(65);
		txtSum.setLayoutY(240 + leftShift);
		txtSum.setVisible(false);

		txtSumWD = setText("Без учета скидки:", 12);
		txtSumWD.setLayoutX(65);
		txtSumWD.setLayoutY(320 + leftShift);


	}

	public static Text setText(String name, int fontWeight) {

        Text text = new Text(name);
        text.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.ITALIC, fontWeight));
        text.setFill(Color.valueOf("#513A2F"));
        return text;
}
}

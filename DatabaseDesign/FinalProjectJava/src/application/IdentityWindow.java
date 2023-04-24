package application;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class IdentityWindow {

	private Rectangle rect;
	private ImageView intView;
	private VBox fNameBox = new VBox();
	private VBox lNameBox = new VBox();
	private HBox checkBoxH = new HBox();
	private StackPane continLayout;
	private Text continTxt;
	private Group root = new Group();
	private Stage primaryStage = new Stage();
	private TextArea firstName, lastName;
	private Button continBtn = new Button();

	public static Label fNameL = new Label();
	public static Label lNameL = new Label();
	public static boolean forRegister;

	public IdentityWindow() {

		setImage();
		setRectForm();
		generateTxtArea();
		generateCheckBox();
		getNameAndContinue();
		nextWindow();

		forRegister = false;

		root.getChildren().addAll(intView, rect, fNameBox, lNameBox, checkBoxH);
		Scene scene = new Scene(root);

        primaryStage.setTitle("Identification");
        primaryStage.setScene(scene);
        primaryStage.show();
	}

	private void nextWindow() {
		    continBtn.setOnAction((ActionEvent event) -> {
			System.out.println(fNameL.getText());
			System.out.println(lNameL.getText());

			System.out.println(forRegister);
			primaryStage.close();
			OrderWindow win2 = new OrderWindow();
		});
	}

	private void continueBtn() {

		int pixel = 137;
		continBtn.setStyle(
                "-fx-background-radius: 5em; "
                + "-fx-min-width: " + pixel + "px; "
                + "-fx-min-height: " + pixel + "px; "
                + "-fx-max-width: " + pixel + "px; "
                + "-fx-max-height: " + pixel + "px; "
                + "-fx-background-color: #FEF9F580;" //FFF0E980
        );
		continTxt = setText("Продолжить", 13);
		continTxt.setLayoutX(315);
		continTxt.setLayoutY(85);
		continTxt.setDisable(true);

		continLayout = new StackPane(
				 continBtn
	    );


		continLayout.setPadding(new Insets(10));
		continLayout.setLayoutX(278);
		continLayout.setLayoutY(2);
		root.getChildren().addAll(continLayout, continTxt);

	}

	private void generateCheckBox() {

		CheckBox checkBox = new CheckBox();
		Text checkTxt = setText("  Впервые с нами", 13);


		checkBox.setStyle(" -fx-background-color: #FCC9AB22;"
				+ "-fx-shadow-highlight-color: #FED8C1;"
				+ "-fx-outer-border: #FB7C4740;"
				+ "-fx-inner-border: #FB7C4740;"
				+ "-fx-effect: dropshadow(gaussian, #FB7C47, 40, 0, 0, 0);");

		checkBox.setOnAction((ActionEvent event) -> {
			forRegister = checkBox.isSelected();

		});

		checkBoxH.setLayoutX(160);
		checkBoxH.setLayoutY(406);
		checkBoxH.getChildren().addAll(checkBox, checkTxt);

	}

	private void generateTxtArea() {

		String style = "-fx-border-color: #FCC9AB; "
	      		+ "-fx-effect: dropshadow(gaussian, #FCC9AB, 40, 0, 0, 0); "
	      		+ "-fx-focus-color: transparent;"
	      		+ "-fx-faint-focus-color: #FED8C150;";

		int namePos = 30;
		firstName = new TextArea();
		firstName.setPrefHeight(10);
		firstName.setPrefWidth(150);

		fNameBox.setLayoutX(150);
		fNameBox.setLayoutY(200 + namePos);
		firstName.setStyle(style);

		lastName = new TextArea();
		lastName.setPrefHeight(10);
		lastName.setPrefWidth(150);

		lNameBox.setLayoutX(150);
		lNameBox.setLayoutY(280 + namePos);
		lastName.setStyle(style);

		Text fName = setText("Имя:", 13);
		Text fNameS = setText(" ", 4);
		Text lName = setText("Фамилия:", 13);
		Text lNameS = setText(" ", 4);

		fNameBox.getChildren().addAll(fName, fNameS, firstName);
		lNameBox.getChildren().addAll(lName, lNameS, lastName);
		fNameBox.setAlignment(Pos.CENTER);
		lNameBox.setAlignment(Pos.CENTER);


	}
	private void getNameAndContinue() {
		firstName.textProperty().addListener(new ChangeListener<String>() {

			public void changed(ObservableValue<? extends String> val, String oldVal, String newVal){

				if(newVal != null) {

					fNameL.setText(newVal);
					if (!lastName.getText().equals(""))
						continueBtn();
				}
			}

		});

		lastName.textProperty().addListener(new ChangeListener<String>() {

			public void changed(ObservableValue<? extends String> val, String oldVal, String newVal){

				if(newVal != null){

					lNameL.setText(newVal);
					if(!firstName.getText().equals(""))
					continueBtn();
				}
			}

		});
	}


	private void setRectForm() {

		Color c1 = Color.valueOf("#FFF9F960");
		Color c2 = Color.valueOf("#FFF0E360");
		Color c3 = Color.valueOf("#FEFAFB60");
		rect = new Rectangle(85, 200, 280, 250);
		rect.setArcWidth(30.0); // Corner radius
		rect.setArcHeight(30.0);
		rect.setFill(c1);
		rect.setStroke(c2);
		rect.setEffect(new DropShadow(20, c3));
	}

	private void setImage() {

		Image intIMG = new Image(getClass().getResource("/Intface3.JPG").toString());
		double koeffResize = 0.6;

		intView = new ImageView(intIMG);
		intView.setPreserveRatio(true);
		intView.setFitWidth(koeffResize * intIMG.getWidth());
	}

	public static Text setText(String name, int fontWeight) {

        Text text = new Text(name);
        text.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.ITALIC, fontWeight));
        text.setFill(Color.valueOf("#520531"));
        return text;
}


}

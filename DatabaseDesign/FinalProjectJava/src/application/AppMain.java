package application;

import javafx.application.Application;
import javafx.stage.Stage;
import connection.*;

public class AppMain  extends Application {


	 @Override
	    public void start(Stage primaryStage) {

		primaryStage.close();
		ReadData  reader = new ReadData();
	    IdentityWindow win1 = new IdentityWindow();
		//WriteData wr = new WriteData();


	 }

		public static void main(String[] args) {
			launch(args);
		}

}
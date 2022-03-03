package manager.email;

import javafx.application.Application;
import javafx.fxml.*;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	@Override
	public void start(Stage stage) throws Exception {
		stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("resources/EmailManager.fxml"))));
		stage.setTitle("Email Manager - ver 1.0");
		stage.setResizable(false);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}

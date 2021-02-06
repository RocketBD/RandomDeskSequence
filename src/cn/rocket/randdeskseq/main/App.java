package cn.rocket.randdeskseq.main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The class used to create GUI
 *
 * @author Rocket
 * @version 2.0
 */
public class App extends Application {
	@Override
	public void start(Stage primaryStage) throws IOException {
		RandomDesk rd = RandomDesk.mainObj;

		//Use JavaFX to output the result
		String[] list = rd.origin.split(",");
		if (list.length != 0) {
			Parent root = FXMLLoader.load(getClass().getResource(ConstPath.window));
			Platform.runLater(() -> Window.nameList = rd.createSequence(list));
			primaryStage.setTitle(rd.lang ? "随机序列" : "Random Sequence");
			primaryStage.setResizable(false);
			primaryStage.setScene(new Scene(root, 350, 400));
			primaryStage.show();
		} else {
			throw new IOException("Input a problematic text file");
		}
	}
}

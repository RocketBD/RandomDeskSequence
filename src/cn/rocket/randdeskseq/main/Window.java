package cn.rocket.randdeskseq.main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * JavaFX application controller class
 *
 * @author Rocket
 * @version 1.0
 */
public class Window {
	@FXML
	Button btn;
	@FXML
	Label label;
	static String[] nameList;
	private int index;

	@FXML
	void initialize() {
		if (!RandomDesk.lang)
			btn.setText("NEXT");
		index = 0;
	}

	@FXML
	void click() {
		if (index < nameList.length) {
			label.setText(nameList[index]);
			index++;
		}
		if (index == nameList.length)
			btn.setDisable(true);
	}
}

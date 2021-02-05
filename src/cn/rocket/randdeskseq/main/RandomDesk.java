package cn.rocket.randdeskseq.main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple program to generate a random sequence of a list of objects.
 *
 * @author RocketBD
 */
public class RandomDesk extends Application {
	private HashMap<Integer, Integer> tabs;
	private boolean sort;
	static boolean lang;

	private void generateTXT(String source, BufferedWriter bw) throws IOException {
		String[] names = source.split(",");
		if (names.length == 0)
			try {
				throw new IOException("Input a problematic text file");
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		String[] sequence = createSequence(names);
		int length = names.length;

		//Print
		if (sort) {
			for (String name : names) {
				bw.write(getTabs(name));
				for (int j = 0; j < length; j++) {
					if (name.equals(sequence[j])) {
						bw.write(Integer.toString(j + 1));
						bw.newLine();
//                    System.out.println(Integer.toString(j+1));
						break;
					}
				}
			}
		} else {
			for (int i = 1; i <= length; i++) {
				bw.write(getTabs(sequence[i - 1]) + i);
				bw.newLine();
			}
		}
	}

	private String getOrigin(String inputPath) {
		InputStream is = RandomDesk.class.getResourceAsStream(inputPath);
		BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
		StringBuilder sb = new StringBuilder();
		String temp;
		try {
			while ((temp = br.readLine()) != null)
				sb.append(temp);
			br.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(3);
		}
		String source = sb.toString().replaceAll(" ", "");
		Matcher m = Pattern.compile("(?<=\\[).+(?=])").matcher(source);

		return m.find() ? m.group() : null;
	}

	private String getTabs(String name) {
		int counter = 0;
		char[] chars = name.toCharArray();
		for (char c : chars)
			if ((int) c >= 0x4e00 && (int) c <= 0x9ffc)
				counter++;
		if (tabs.containsKey(counter)) {
			StringBuilder sb = new StringBuilder(name);
			for (int i = 0; i < tabs.get(counter); i++)
				sb.append('\t');
			return sb.toString();
		}
		return name + "\t\t";
	}

	private String[] createSequence(String[] names) {
		//Process and randomize the namelist
		int length = names.length;
		String[] sequence = new String[length];
		LinkedList<String> list = new LinkedList<>();
		Collections.addAll(list, names);
		for (int i = 0; i < length; i++) {
			int position = (int) (Math.random() * (length - i));
			sequence[i] = list.get(position);
			list.remove(position);
		}
		return sequence;
	}

	@Override
	public void start(Stage primaryStage) throws IOException {
		String jarPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		String globalPath = new File(jarPath).getParentFile().getPath();

		//Get properties
		Properties set = new Properties();
		try {
			set.load(RandomDesk.class.getResourceAsStream(ConstPath.set));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		boolean mode = Boolean.parseBoolean(set.getProperty("mode"));
		sort = Boolean.parseBoolean(set.getProperty("sortingByOrigin"));
		tabs = new HashMap<>();
		tabs.put(2, Integer.valueOf(set.getProperty("for2chars")));
		tabs.put(3, Integer.valueOf(set.getProperty("for3chars")));
		tabs.put(4, Integer.valueOf(set.getProperty("for4chars")));
		tabs.put(5, Integer.valueOf(set.getProperty("for5chars")));
		String importPath = globalPath + "/" + set.getProperty("importFile");
		String exportPath = set.getProperty("exportFile");
		lang = Boolean.parseBoolean(set.getProperty("language"));

		//Input
		File inputFile = new File(importPath);
		String origin = getOrigin(inputFile.exists() ? importPath : ConstPath.names);
		if (origin == null) {
			try {
				throw new IOException("Input a problematic text file");
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(2);
			}
		}

		if (mode) {
			//Generate output file
			File out = new File(globalPath + "/" + exportPath);
			try {
				//noinspection ResultOfMethodCallIgnored
				out.createNewFile();
				OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(out), StandardCharsets.UTF_8);
				BufferedWriter bw = new BufferedWriter(osw);
				generateTXT(origin, bw);
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			System.exit(0);
		} else {
			//Use JavaFX to output the result
			String[] list = origin.split(",");
			if (list.length != 0) {
				Parent root = FXMLLoader.load(getClass().getResource(ConstPath.window));
				Platform.runLater(() -> Window.nameList = createSequence(list));
				primaryStage.setTitle(lang ? "随机序列" : "Random Sequence");
				primaryStage.setResizable(false);
				primaryStage.setScene(new Scene(root, 350, 400));
				primaryStage.show();
			} else {
				try {
					throw new IOException("Input a problematic text file");
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(1);
				}
			}

		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}

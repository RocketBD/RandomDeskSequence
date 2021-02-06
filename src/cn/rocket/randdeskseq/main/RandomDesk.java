package cn.rocket.randdeskseq.main;

import javafx.application.Application;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
 * @version 2.0
 */
public class RandomDesk {
	private HashMap<Integer, Integer> tabs;
	private boolean sort;
	boolean lang;
	static RandomDesk mainObj;
	String origin;

	private void generateTXT(String source, BufferedWriter bw) throws IOException {
		String[] names = source.split(",");
		if (names.length == 0)
			throw new IOException("Input a problematic text file");
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
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(3);
		}

		//Delete white spaces
		String source = sb.toString().replaceAll(" ", "");

		//Get names
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

	String[] createSequence(String[] names) {
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

	private void launch(boolean copy) throws IOException {
		//Get jar root path
		String jarPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		String globalPath = new File(jarPath).getParentFile().getPath() + "/";

		//Process generating template
		if (copy) {
			InputStream in = this.getClass().getResourceAsStream(ConstPath.template);
			File out = new File(globalPath + "settings.properties");
			if (!out.exists())
				Files.copy(in, out.toPath());
			else
				System.err.println("Template created! If you want to create it again, " +
						"please first delete it.");
			return;
		}

		//Get properties
		DefaultValue.getValues(globalPath);
		File setP = new File(globalPath + "settings.properties");
		String importPath = DefaultValue._importPath;
		String exportPath = DefaultValue._exportPath;
		boolean mode = DefaultValue._mode;
		sort = DefaultValue._sort;
		tabs = DefaultValue._tabs;
		lang = DefaultValue._lang;
		if (setP.exists()) {
			String temp;
			Properties set = new Properties();
			FileInputStream fis = new FileInputStream(setP);
			set.load(fis);
			mode = Boolean.parseBoolean(set.getProperty("mode", Boolean.toString(mode)));
			sort = Boolean.parseBoolean(set.getProperty("sortingByOrigin", Boolean.toString(sort)));
			for (int i = 2; i <= 5; i++) {
				if (!(temp = set.getProperty("for" + i + "chars")).equals(""))
					tabs.put(i, Integer.valueOf(temp));
			}
			importPath = !(temp = set.getProperty("importFile")).equals("") ? temp : importPath;
			exportPath = !(temp = set.getProperty("exportFile")).equals("") ? temp : exportPath;
			lang = Boolean.parseBoolean(set.getProperty("language", Boolean.toString(lang)));
			fis.close();
		}


		//Input & check file content
		File inputFile = new File(importPath);
		origin = getOrigin(inputFile.exists() ? importPath : ConstPath.names);
		if (origin == null)
			throw new IOException("Input a problematic text file");

		if (mode) {
			//Generate output file
			File out = new File(exportPath);
			//noinspection ResultOfMethodCallIgnored
			out.createNewFile();
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(out), StandardCharsets.UTF_8);
			BufferedWriter bw = new BufferedWriter(osw);
			generateTXT(origin, bw);
			bw.close();
		} else {
			Application.launch(App.class);
		}
	}

	public static void main(String[] args) {
		boolean copy = args.length > 0 && args[0].equals("-template");
		try {
			mainObj = new RandomDesk();
			mainObj.launch(copy);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class DefaultValue {
	static boolean _sort;
	static boolean _lang;
	static HashMap<Integer, Integer> _tabs;
	static String _importPath;
	static String _exportPath;
	static boolean _mode;

	static void getValues(String dest) throws IOException {
		Properties set = new Properties();
		InputStream is = RandomDesk.class.getResourceAsStream(ConstPath.defaultPrs);
		set.load(is);
		_mode = Boolean.parseBoolean(set.getProperty("mode"));
		_sort = Boolean.parseBoolean(set.getProperty("sortingByOrigin"));
		_tabs = new HashMap<>();
		_tabs.put(2, Integer.valueOf(set.getProperty("for2chars")));
		_tabs.put(3, Integer.valueOf(set.getProperty("for3chars")));
		_tabs.put(4, Integer.valueOf(set.getProperty("for4chars")));
		_tabs.put(5, Integer.valueOf(set.getProperty("for5chars")));
		_importPath = dest + set.getProperty("importFile");
		_exportPath = dest + set.getProperty("exportFile");
		_lang = Boolean.parseBoolean(set.getProperty("language"));
		is.close();
	}
}

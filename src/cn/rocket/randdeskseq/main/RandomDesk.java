package cn.rocket.randdeskseq.main;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RandomDesk {
	private String globalPath;

	private void generate(String source, BufferedWriter bw) throws IOException {
		String[] names = source.split(",");
		int length = names.length;
		String[] sequence = new String[length];
		LinkedList<String> list = new LinkedList<>();
		Collections.addAll(list, names);
		for (int i = 0; i < length; i++) {
			int position = (int) (Math.random() * (length - i));
			sequence[i] = list.get(position);
			list.remove(position);
		}
		for (String name : names) {
			if (name.length() <= 4)
				bw.write(name + "\t\t\t");
			else
				bw.write(name + "\t\t");
//            System.out.print(name+"\t\t");
			for (int j = 0; j < length; j++) {
				if (name.equals(sequence[j])) {
					bw.write(Integer.toString(j + 1));
					bw.newLine();
//                    System.out.println(Integer.toString(j+1));
					break;
				}
			}
		}
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	private String getOrigins() throws IOException {
		InputStream is = RandomDesk.class.getResourceAsStream(ConstPath.names);
		BufferedReader br = new BufferedReader(new InputStreamReader(is,StandardCharsets.UTF_8));
		StringBuilder sb = new StringBuilder();
		String temp;
		while ((temp= br.readLine())!=null)
			sb.append(temp);
//        System.out.println(source);
		br.close();
		is.close();
		String source = sb.toString().replaceAll(" ","");
		Matcher m = Pattern.compile("(?<=\\[).+(?=])").matcher(source);
		m.find();
		return m.group();
	}

	private void launch() {
		try {
			String origin = getOrigins();
			String jarPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
			globalPath = new File(jarPath).getParentFile().getPath();
			File settings = new File(globalPath+"/");
			File out = new File(globalPath + "/output.txt");
			if (!out.exists())
				//noinspection ResultOfMethodCallIgnored
				out.createNewFile();
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(out), StandardCharsets.UTF_8);
			BufferedWriter bw = new BufferedWriter(osw);
			generate(origin, bw);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	private byte processName(String name){
		byte counter = 0;
		char[] chars = name.toCharArray();
		for(char c : chars)
			if((int)c>=0x4e00&&(int)c<=0x9ffc)
				counter++;
		return counter;
	}

	public static void main(String[] args) {
		new RandomDesk().launch();
	}
}

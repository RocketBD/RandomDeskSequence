package cn.rocket.randdeskseq.main;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RandomDesk {
    private static void generate(@NotNull String source, BufferedWriter bw) throws IOException {
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
    private String getOrigin() throws IOException {
        InputStream is = RandomDesk.class.getResourceAsStream("/cn/rocket/randdeskseq/main/names.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String source = br.readLine();
//        System.out.println(source);
        br.close();
        is.close();
        Matcher m = Pattern.compile("(?<=\\[).+(?=])").matcher(source);
        m.find();
        return m.group();
    }

    private void launch() {
        try {
            String origin = getOrigin();
            String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
            String _path = new File(path).getParentFile().getPath() + "/output.txt";
//            System.out.println(_path);
            File out = new File(_path);
            if (!out.exists())
                //noinspection ResultOfMethodCallIgnored
                out.createNewFile();
            FileWriter fw = new FileWriter(out);
            BufferedWriter bw = new BufferedWriter(fw);
            generate(origin, bw);
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) {
        new RandomDesk().launch();
    }
}

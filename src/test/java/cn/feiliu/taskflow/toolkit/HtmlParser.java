package cn.feiliu.taskflow.toolkit;

import java.io.*;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-10-07
 */
public class HtmlParser {
    public static void main(String[] args) throws IOException {
        String path = "/Users/kevin/Desktop/web/";
        String dist = "/Users/kevin/Desktop/web/dist";
        File[] files = new File(path).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".html");
            }
        });
        File navFile = new File(path + "nav.html");
        File footerFile = new File(path + "footer.html");
        for (File file : files) {
            String content = readFile(file);
            if (content.indexOf("<!--导航-->") != -1 && content.indexOf("<!--页尾-->") != -1) {
                content = content.replace("$(\"#navigation\").load(\"./nav.html?rn=6a93310f\");", "");
                content = content.replace("$(\"#footerDiv\").load(\"./footer.html?rn=6a93310f\");", "");
                content = content.replace("<!--导航-->", readFile(navFile));
                content = content.replace("<!--页尾-->", readFile(footerFile));
                System.out.println(file.getName());
                System.out.println(content);
                writeFile(content, new File(dist + "/" + file.getName()));
            } else {
                writeFile(content, new File(dist + "/" + file.getName()));
            }
        }
    }

    static void writeFile(String content, File file) throws IOException {
        try (FileWriter fileWriter = new FileWriter(file)) {
            try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                bufferedWriter.write(content);
            }
        }
    }

    public static String readFile(File file) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (FileReader fileReader = new FileReader(file)) {
            try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                String line = bufferedReader.readLine();
                while (line != null) {
                    builder.append(line).append("\n");
                    line = bufferedReader.readLine();
                }
            }
        }
        return builder.toString();
    }
}

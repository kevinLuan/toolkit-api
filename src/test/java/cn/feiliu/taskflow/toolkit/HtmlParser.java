package cn.feiliu.taskflow.toolkit;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-10-07
 */
public class HtmlParser {
    static String path = "/Users/kevin/Desktop/web/";
    static String dist = "/Users/kevin/Desktop/web/dist";

    public static void main(String[] args) throws IOException {
        File[] files = new File(path).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".html");
            }
        });
        String randomStr = Long.toHexString(System.currentTimeMillis()).substring(0, 8);
        String navContent = processNavAndFooter(new File(path + "nav.html"), randomStr);
        String footerContent = processNavAndFooter(new File(path + "footer.html"), randomStr);
        for (File file : files) {
            String content = readFile(file);
            content = replaceRandomString(content, randomStr);
            if (content.indexOf("<!--导航-->") != -1 && content.indexOf("<!--页尾-->") != -1) {
                content = content.replace("$(\"#navigation\").load(\"./nav.html\");", "");
                content = content.replace("$(\"#footerDiv\").load(\"./footer.html\");", "");
                content = content.replace("<!--导航-->", navContent);
                content = content.replace("<!--页尾-->", footerContent);
                writeFile(content, new File(dist + "/" + file.getName()));
            } else {
                writeFile(content, new File(dist + "/" + file.getName()));
            }
        }
    }

    private static String processNavAndFooter(File navFile, String randomStr) throws IOException {
        String content = readFile(navFile);
        content = replaceRandomString(content, randomStr);
        writeFile(content, new File(dist + "/" + navFile.getName()));
        return content;
    }

    private static String replaceRandomString(String originalText, String newRandomStr) {
        Pattern pattern = Pattern.compile("\\?rn=[a-zA-Z0-9]{1,64}\"");
        Matcher matcher = pattern.matcher(originalText);
        originalText = matcher.replaceAll("?rn=" + newRandomStr + "\"");
        return originalText;
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

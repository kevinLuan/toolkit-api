package cn.feiliu.taskflow.toolkit;

import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class FreemarkerExample {
    public static void main(String[] args) throws Exception {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        String templateStr = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<title>${title}</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>${title}</h1>\n" +
                "<p>${content}</p>\n" +
                "</body>\n" +
                "</html>";

        Template temp = new Template("temp", templateStr, cfg);
        Map<String, Object> root = new HashMap<>();
        root.put("title", "欢迎");
        root.put("content", "这是一个示例页面");
        Writer out = new StringWriter();
        temp.process(root, out);
        System.out.println(out.toString());
    }
}
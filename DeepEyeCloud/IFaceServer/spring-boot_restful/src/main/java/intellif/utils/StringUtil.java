package intellif.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

public class StringUtil extends StringUtils {
    /**
     * 截取分割字符后字符串
     * 
     * @param sStr
     * @param separate
     */
    public static String[] separateStr(String sStr, String separate1, String separate2) {
        try {
            if (null != sStr) {
                String ss[] = sStr.trim().split("\\" + separate1);
                String fStr = ss[ss.length - 1];
                String ss1[] = fStr.trim().split("\\" + separate2);
                return ss1;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isChineseChar(String str) {
        boolean temp = false;
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            temp = true;
        }
        return temp;
    }

    /**
     * 去除路径中文名称，重命名
     * 
     * @param dir
     * @return
     */
    public static String filterChineseWord(String dir) {

        String[] ss = dir.split("/");
        String fileExt = FilenameUtils.getExtension(ss[ss.length - 1]);
        StringBuffer newPath = new StringBuffer();
        for (int i = 0; i < ss.length - 1; i++) {
            if (null != ss[i] && !"".equals(ss[i])) {
                newPath.append("/");
                newPath.append(ss[i]);
            }
        }
        String randomStr = String.valueOf(Math.round(Math.random() * 1000000));
        String reName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(new Date()) + "_" + randomStr + "." + fileExt;
        File file = new File(dir);
        file.renameTo(new File(newPath.toString() + "/" + reName));
        return newPath.toString() + "/" + reName;
    }

    public static String escapeExprSpecialWord(String keyword) {
        if (StringUtils.isNotBlank(keyword)) {
            String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|" };
            for (String key : fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
        }
        return keyword;
    }

    /**
     * 将驼峰转下划线，如："helloWorld"转为"hello_world"
     * @param s
     * @return
     */
    public static String toUnderlineName(String s) {
        if (s == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            boolean nextUpperCase = true;

            if (i < (s.length() - 1)) {
                nextUpperCase = Character.isUpperCase(s.charAt(i + 1));
            }

            if ((i >= 0) && Character.isUpperCase(c)) {
                if (!upperCase || !nextUpperCase) {
                    if (i > 0)
                        sb.append("_");
                }
                upperCase = true;
            } else {
                upperCase = false;
            }

            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }

    /**
     * 将下划线转驼峰，如： "hello_world"转为"helloWorld"
     * @param s
     * @return
     */
    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        }

        s = s.toLowerCase();

        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == '_') {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
     * 将下划线转驼峰，并将首字母大写，如：将 "hello world"转为"HelloWorld"
     * @param s
     * @return
     */
    public static String toCapitalizeCamelCase(String s) {
        if (s == null) {
            return null;
        }
        return capitalize(toCamelCase(s));
    }
}

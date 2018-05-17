package intellif.dto.mqtt;

import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * For import station data
 * Created by Zheng Xiaodong on 2017/5/6.
 */
public class GenerateDepartment {
    private static final String PATH = "F:\\work\\station.txt";
    private static final String OUTPATH = "F:\\work\\station2.txt";
    private static long nextId = 1000;

    public static void main(String ... args) throws IOException {

//        String[] s = "/1/2/3".split("/");
//        for (int i = 0; i < s.length; i++)
//            System.out.println("|" + s[i] + "|");
//
//        return;

        FileInputStream fis = new FileInputStream(PATH);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        String line = "";

        Map<String, Map<String, String>> map = new LinkedHashMap<>();
        int lineNumber = 1;
        while ((line = br.readLine()) != null) {
            parseLine(map, line, lineNumber++);
        }

        String resultSql = generateSql(map);

        outputToFile(resultSql);
    }

    private static void parseLine(Map<String, Map<String, String>> map, String line, int lineNumber) {
        String[] arr = line.split("/");
        String parentId = "";
        int newCount = 0;
        String path = "";
        for (int i = 0; i < arr.length; i++) {
            path += arr[i] + "/";
            Map<String, String> idMap = new HashMap<>();
            if (StringUtils.isNotBlank(arr[i])) {
                if (!map.containsKey(path)) {
                    if (i > 1) {
                        parentId = map.get(getParentPath(path)).get("id");
                        if (StringUtils.isBlank(parentId)) {
                            System.err.println("parentId is empty: " + arr[i]);
                            parentId = "0";
                        }
                    }
                    idMap.put("parentId", parentId);
                    idMap.put("id", "" + nextId++);
                    idMap.put("name", arr[i]);
                    idMap.put("path", path);
                    map.put(path, idMap);
                    newCount++;
                } else if (i == arr.length - 1) {
                    System.err.println("Line " + lineNumber + ": Duplicate station found, path is: " + path);
                }
            } else if (i != 0) {
                    System.err.println("Blank name found!");
            }

            if (newCount > 1) {
                System.err.println("Line " + lineNumber  + ": has many new entry." );
            }
        }
    }

    private static String generateSql(Map<String, Map<String, String>> map) {
        StringBuilder sql = new StringBuilder();
        for (Map.Entry<String, Map<String, String>> entry : map.entrySet()) {
            sql.append(generateOneSql(entry.getValue().get("id"), entry.getValue().get("parentId"), entry.getValue().get("name")));
            sql.append("\n");
        }
        return sql.toString();
    }

    private static String generateOneSql(String id, String parentId, String name) {
        String sql = "replace into intellif_base.t_police_station (id, " +
                "created, updated, person_threshold, special_total_num, special_use_num, " +
                "user_count, parent_id, station_name) values (" + id + ", now(), now(), 0, 0, 0, 0, " +
                parentId + ", '" + name + "');";
        return sql;
    }

    private static void outputToFile(String result) {
        try{
            File file = new File(OUTPATH);

            if(!file.exists()){
                file.createNewFile();
            }

            FileWriter fileWritter = new FileWriter(file,false);
            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
            bufferWritter.write(result);
            bufferWritter.flush();
            bufferWritter.close();

        }catch(IOException e){
            e.printStackTrace();
        } finally {

        }
    }

    private static String getParentPath(String path) {
        return path.substring(0, path.substring(0, path.length() - 1).lastIndexOf("/")) + "/";
    }
}

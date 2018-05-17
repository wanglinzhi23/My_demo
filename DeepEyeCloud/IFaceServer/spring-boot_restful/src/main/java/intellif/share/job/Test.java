package intellif.share.job;

import intellif.dto.MobileCollectPersonDto;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.PropertyNamingStrategy;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Zheng Xiaodong on 2017/6/20.
 */
public class Test {
    public static String path = "H:\\downloads\\collect2017-06-2009\\person.txt";
    public static void main(String[] args) {
        parsePersons();
    }

    private static List<MobileCollectPersonDto> parsePersons() {
        List<MobileCollectPersonDto> persons = new ArrayList<>();
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(path));
            String content = new String(bytes, Charset.forName("GBK"));
            ObjectMapper mapper = new ObjectMapper();
            mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
            persons.addAll(Arrays.asList(mapper.readValue(content, MobileCollectPersonDto[].class)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return persons;
    }
}

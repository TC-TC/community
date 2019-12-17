package life.tc.community.cache;

import life.tc.community.dto.TagDTO;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TagCache {
    public static List<TagDTO> get() {
        List<TagDTO> tagDTOS = new ArrayList<>();

        TagDTO program = new TagDTO();
        program.setCategoryName("开发语言");
        program.setTags(Arrays.asList("js","php","css","html","java","node","python","C++","C#"));
        tagDTOS.add(program);

        TagDTO framework = new TagDTO();
        framework.setCategoryName("平台框架");
        framework.setTags(Arrays.asList("spring","django","yii","koa","flask"));
        tagDTOS.add(framework);

        TagDTO server = new TagDTO();
        server.setCategoryName("服务器");
        server.setTags(Arrays.asList("Linux","apache","tomcat","windows-server"));
        tagDTOS.add(server);

        TagDTO db = new TagDTO();
        db.setCategoryName("数据库");
        db.setTags(Arrays.asList("mysql","sql","h2","sqlserver","oracle"));
        tagDTOS.add(db);

        TagDTO tool = new TagDTO();
        tool.setCategoryName("开发工具");
        tool.setTags(Arrays.asList("git","maven","vim","ide","github"));
        tagDTOS.add(tool);

        return tagDTOS;
    }

    //校验标签是否非法
    public static String  Invaild(String tags){
        String[] split= StringUtils.split(tags,",");
        List<TagDTO> tagDTOS = get();
        //将所有标签拿到放到tagDTOS
        List<String> tagList = tagDTOS.stream().flatMap(tag->tag.getTags().stream()).collect(Collectors.toList());
        //拿到所有split中tagList不包含的部分，就是所有的非法标签
        String invaild = Arrays.stream(split).filter(t->!tagList.contains(t)).collect(Collectors.joining(","));

        return invaild;
    }
}

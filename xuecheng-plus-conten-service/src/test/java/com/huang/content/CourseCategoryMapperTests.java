package com.huang.content;

import com.huang.content.mapper.CourseCategoryMapper;
import com.huang.content.model.dto.CourseCategoryTreeDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class CourseCategoryMapperTests {


    @Autowired
    CourseCategoryMapper courseCategoryMapper;

    @Test
    void testqueryTreeNodes() {
        List<CourseCategoryTreeDto> categoryTreeDtos =
                courseCategoryMapper.selectTreeNodes("1");
        System.out.println(categoryTreeDtos);
    }

}

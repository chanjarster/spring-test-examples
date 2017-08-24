package me.chanjar.annotation.jsontest.ex1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = SimpleJsonTest.class)
@JsonTest
public class SimpleJsonTest extends AbstractTestNGSpringContextTests {

  @Autowired
  private JacksonTester<Foo> json;

  @Test
  public void testSerialize() throws Exception {
    Foo details = new Foo("Honda", 12);
    // 使用通包下的json文件测试结果是否正确
    assertThat(this.json.write(details)).isEqualToJson("expected.json");
    // 或者使用基于JSON path的校验
    assertThat(this.json.write(details)).hasJsonPathStringValue("@.name");
    assertThat(this.json.write(details)).extractingJsonPathStringValue("@.name").isEqualTo("Honda");
    assertThat(this.json.write(details)).hasJsonPathNumberValue("@.age");
    assertThat(this.json.write(details)).extractingJsonPathNumberValue("@.age").isEqualTo(12);
  }

  @Test
  public void testDeserialize() throws Exception {
    String content = "{\"name\":\"Ford\",\"age\":13}";
    Foo actual = this.json.parseObject(content);
    assertThat(actual).isEqualTo(new Foo("Ford", 13));
    assertThat(actual.getName()).isEqualTo("Ford");
    assertThat(actual.getAge()).isEqualTo(13);

  }

}

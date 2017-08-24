package me.chanjar.annotation.jsontest.ex3;

import me.chanjar.annotation.jsontest.ex1.Foo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ThinJsonTest.class)
public class ThinJsonTest extends AbstractTestNGSpringContextTests {

  @Autowired
  private JacksonTester<Foo> json;

  @Test
  public void testSerialize() throws Exception {
    Foo details = new Foo("Honda", 12);
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

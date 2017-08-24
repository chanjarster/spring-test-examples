package me.chanjar.annotation.jsontest.ex2;

import me.chanjar.annotation.jsontest.ex1.Foo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = { JsonComponentJacksonTest.class, FooJsonComponent.class })
@JsonTest
public class JsonComponentJacksonTest extends AbstractTestNGSpringContextTests {

  @Autowired
  private JacksonTester<Foo> json;

  @Test
  public void testSerialize() throws Exception {
    Foo details = new Foo("Honda", 12);
    assertThat(this.json.write(details).getJson()).isEqualTo("\"name=Honda,age=12\"");
  }

  @Test
  public void testDeserialize() throws Exception {
    String content = "\"name=Ford,age=13\"";
    Foo actual = this.json.parseObject(content);
    assertThat(actual).isEqualTo(new Foo("Ford", 13));
    assertThat(actual.getName()).isEqualTo("Ford");
    assertThat(actual.getAge()).isEqualTo(13);

  }

}

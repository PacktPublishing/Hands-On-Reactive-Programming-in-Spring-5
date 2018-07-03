package org.rpis5.chapters.chapter_03.conversion_problem;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;


@SpringBootTest(
	webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
	classes = {ConversionProblemApp.class, MyController.class, TestController.class}
)
@RunWith(SpringRunner.class)
public class MyControllerTest {

	@Test
	public void testMyControllerResponse() {
		RestTemplate template = new RestTemplate();

		String object = template.getForObject("http://localhost:8080", String.class);

		MatcherAssert.assertThat(object, Matchers.equalTo("Hello World"));
	}
}

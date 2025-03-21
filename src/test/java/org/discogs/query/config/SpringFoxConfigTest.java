package org.discogs.query.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import springfox.documentation.spring.web.plugins.Docket;

@SpringBootTest
class SpringFoxConfigTest {

  @Autowired private Docket docket;

  @Test
  void testDocketBean() {
    // Assert that the Docket bean is created and not null
    assertNotNull(docket, "Docket bean should be created by the " + "SpringFoxConfig class.");
  }
}

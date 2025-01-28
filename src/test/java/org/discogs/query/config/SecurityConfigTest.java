package org.discogs.query.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@Disabled
@WebMvcTest(SecurityConfig.class)
class SecurityConfigTest {

  @Autowired private MockMvc mockMvc;

  @Test
  @WithMockUser
  // Mock authentication for the test
  void testSecurityConfiguration() throws Exception {
    mockMvc.perform(get("/")).andExpect(status().isOk()); // Ensure that the endpoint is
    // accessible
  }

  @Test
  void testSecurityConfigurationWithoutAuth() throws Exception {
    mockMvc.perform(get("/")).andExpect(status().isOk()); // Ensure that the endpoint is
    // accessible without auth
  }
}

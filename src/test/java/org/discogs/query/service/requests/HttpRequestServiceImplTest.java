package org.discogs.query.service.requests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.discogs.query.interfaces.HttpRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

class HttpRequestServiceImplTest {

  private RestTemplate restTemplate;
  private HttpRequestService httpRequestService;

  @BeforeEach
  public void setUp() {
    restTemplate = mock(RestTemplate.class);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpRequestService = new HttpRequestServiceImpl(restTemplate, httpHeaders);
  }

  @Test
  void testExecuteRequest() throws Exception {
    String url = "http://example.com";
    String expectedResponse = "response";
    ResponseEntity<String> responseEntity = ResponseEntity.ok(expectedResponse);

    when(restTemplate.exchange(
            eq(url), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
        .thenReturn(responseEntity);

    String result = httpRequestService.executeRequest(url, String.class);

    assertEquals(expectedResponse, result);

    ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
    verify(restTemplate)
        .exchange(eq(url), eq(HttpMethod.GET), entityCaptor.capture(), eq(String.class));
  }

  @Test
  void testExecuteRequestThrowsException() {
    String url = "http://example.com";
    when(restTemplate.exchange(
            eq(url), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
        .thenThrow(new RuntimeException("Request failed"));

    assertThrows(
        RuntimeException.class, () -> httpRequestService.executeRequest(url, String.class));
  }
}

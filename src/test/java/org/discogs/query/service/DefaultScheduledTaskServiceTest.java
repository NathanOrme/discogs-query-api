package org.discogs.query.service;


import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DefaultScheduledTaskServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private DefaultScheduledTaskService scheduledTaskService;

    public DefaultScheduledTaskServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendRequest_Success() {
        // Prepare mock response
        ResponseEntity<String> mockResponse = ResponseEntity.ok("Mock Response");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockResponse);

        // Execute the method
        scheduledTaskService.sendRequest();

        // Verify interactions and assert
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void sendRequest_Error() {
        // Prepare mock behavior for error
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RuntimeException("Mock exception"));

        // Execute the method
        // The method should handle the exception internally
        assertDoesNotThrow(() -> scheduledTaskService.sendRequest());

        // Verify interaction
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
    }
}

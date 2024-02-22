package com.inner.consulting.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@FeignClient(name = "empleador-service", url = "http://localhost:8084")
public interface EmpleadorClient {

    @GetMapping("/generatedId")
    ResponseEntity<String> generateId();
}

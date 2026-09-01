package com.example.proyecto.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ReniecService {

    @Value("${decolecta.token}")
    private String token;

    public Map consultarDni(String dni) {

        String url =
                "https://api.decolecta.com/v1/reniec/dni?numero=" + dni;

        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(token);

        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity =
                new HttpEntity<>(headers);

        RestTemplate restTemplate =
                new RestTemplate();

        ResponseEntity<Map> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        Map.class
                );

        return response.getBody();
    }
}
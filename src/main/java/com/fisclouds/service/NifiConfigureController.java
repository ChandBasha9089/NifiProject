package com.fisclouds.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fisclouds.FixedValues;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.FileInputStream;
import java.util.Properties;

@Service
public class NifiConfigureController {



    public String createDBCPConnectionPool()throws Exception {
        String url = FixedValues.controllerUrl;
      // Replace with your actual process group ID


        FileInputStream fileInputStream = new FileInputStream(FixedValues.resouceApplication);

        Properties properties = new Properties();
        properties.load(fileInputStream);

        String newProcessgroupId = properties.getProperty("new_processgroup_id");
        url = url.replace("{id}", newProcessgroupId);

        ObjectMapper objectMapper = new ObjectMapper();
        Object jsonObject = objectMapper.readValue("{" +
                "  \"revision\": {" +
                "    \"version\": 0" +
                "  }," +
                "  \"component\": {" +
                "    \"type\": \"org.apache.nifi.dbcp.DBCPConnectionPool\"," +
                "    \"name\": \"Hyderabad\"," +
                "    \"properties\": {" +
                "      \"Database Connection URL\": \"jdbc:mysql://localhost:3306/shop\"," +
                "      \"Database Driver Class Name\": \"com.mysql.cj.jdbc.Driver\"," +
                "      \"Database Driver Location\": \"C:/Users/chand shaik/Downloads/mysql-connector-java-8.0.30.jar\"," +
                "      \"Database User\": \"root\"," +
                "      \"Password\": \"root\"" +
                "    }" +
                "  }" +
                "}", Object.class);

        // Convert JSON object to string
        String requestBody = objectMapper.writeValueAsString(jsonObject);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        if (response.getStatusCodeValue() == 201) {

            System.out.println("Controller Service created successfully.");
            return "Controller Service created successfully.";
        } else {
            System.err.println("Failed to create Controller Service. Status code: " + response.getStatusCode());

            return "Controller Service Failed to Create";
        }
    }

}

package com.fisclouds.controller;


import com.fisclouds.FixedValues;
import com.fisclouds.connection.ConnectProcessors;
import com.fisclouds.service.ProcessorTasks;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

@RestController
public class CreateWorkFlow {


    @Autowired
    private ProcessorTasks processorTasks;


    @Autowired
    private ConnectProcessors connectProcessors;


    @PostMapping("/createworkflow")
    public Object createOneWorkFlow() throws Exception {

        String  processGroup =(String) processorTasks.creatingNewProcessGroup();
        String twoProcessorsInNewProcessGroup = processorTasks.createTwoProcessorsInNewProcessGroup();


        String s = connectProcessors.connectTwoProcessors();


        return processGroup+"--"+twoProcessorsInNewProcessGroup+"--"+s;


    }

    @PostMapping("/createprocessgroup")
    public Object createNewProcesGroup() throws Exception {
       return processorTasks.creatingNewProcessGroup();
    }


    @PostMapping("/twonewprocessors")
    public Object createNewProcesGroupInNewPGroup() throws Exception {

        return processorTasks.createTwoProcessorsInNewProcessGroup();

    }


    @GetMapping("/connect")
    public Object connectProcessors() throws Exception {
        return connectProcessors.connectTwoProcessors();

    }





    @GetMapping("/update")
    public Object configureProcessor() throws Exception {


        FileInputStream fileInputStream = new FileInputStream(new File(FixedValues.resouceApplication));


        Properties properties = new Properties();

        properties.load(fileInputStream);

        String sourceId = properties.getProperty("sourceId");
      //  String processorId = "your_processor_id"; // Replace with your processor ID
        String url = "http://localhost:38080/nifi-api/processors/" + sourceId;


        String requestBody = "{\n" +
                "  \"revision\": {\n" +
                "    \"version\": 3\n" +
                "  },\n" +
                "  \"component\": {\n" +
                "    \"id\": \"" + sourceId + "\",\n" +
                "    \"config\": {\n" +
                "      \"properties\": {\n" +
                "        \"HTTP Method\": \"GET\",\n" +
                "        \"HTTP URL\": \"http://localhost:8099/all\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);

        System.out.println("Response status code: " + responseEntity.getStatusCodeValue());
        System.out.println("Response body: " + responseEntity.getBody());

        return responseEntity.getBody();
    }




}
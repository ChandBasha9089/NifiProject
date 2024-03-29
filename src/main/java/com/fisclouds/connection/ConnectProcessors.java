package com.fisclouds.connection;

import com.fisclouds.FixedValues;
import com.fisclouds.entity.ConnectionDTO;
import com.fisclouds.service.ProcessorTasks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

@Service
public class ConnectProcessors {


    @Autowired
    private ProcessorTasks processorTasks;


    @Autowired
    private ConnectionDTO connectionDTO;



    public String connectTwoProcessors() throws Exception {


        InputStream fileInputStream = new FileInputStream(FixedValues.resouce);

        Yaml yaml = new Yaml();
        Map<String,Object> load = yaml.load(fileInputStream);

        String o = (String) load.get("process_group_id");


        FileInputStream fileInputStream1 = new FileInputStream(FixedValues.resouceApplication);

        Properties properties = new Properties();


        properties.load(fileInputStream1);


        String newProcessgroupId = properties.getProperty("new_processgroup_id");

        String sourceId = properties.getProperty("sourceId");

        String destinationId = properties.getProperty("destinationId");


//        String newProcessGroupId = processorTasks.processorId.get(FixedValues.newProcessGroupId);
//        String sourceProcessorId = processorTasks.processorId.get(FixedValues.sourceProcessorId);
//        String destinationProcessorId = processorTasks.processorId.get(FixedValues.destinationProcessorId);
        return connectProcessors(newProcessgroupId,sourceId,destinationId);



    }


    public String connectProcessors(String processGroupId, String sourceId, String destinationId) {
        String url = FixedValues.baseURL + FixedValues.processGroup + "/" + processGroupId + "/connections";

        String relationshipName = "myConnection"; // Specify a relationship name

        String requestBody = "{" +
                "\"revision\": {\"version\": 0}," +
                "\"component\": {" +
                "\"name\": \"ConnectProcessor\"," +
                "\"source\": {\"id\": \"" + sourceId + "\", \"groupId\": \"" + processGroupId + "\", \"type\": \"PROCESSOR\"}," +
                "\"destination\": {\"id\": \"" + destinationId + "\", \"groupId\": \"" + processGroupId + "\", \"type\": \"PROCESSOR\"}," +
                "\"selectedRelationships\": [\"" + relationshipName + "\"]" + // Specify the relationship
                "}" +
                "}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        if (response.getStatusCode() == HttpStatus.CREATED) {
            System.out.println("Connection created successfully.");
            return "Connected";
        } else {
            System.out.println("Failed to create connection. Status code: " + response.getStatusCode());
            return "Not Connected";
        }

    }
}

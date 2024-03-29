package com.fisclouds.service;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fisclouds.FixedValues;
import com.fisclouds.entity.ConnectionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.Yaml;

@Service
public class ProcessorTasks {


	public Map<String,String > processorId=new HashMap<>();

	public String newProcessGroupId;



	@Autowired
	private ConnectionDTO connectionDTO;



	//Creating Processors In Apache Nifi From Parent ProcessGroupId
	public String createTwoProcessors() throws Exception {


		Yaml yaml = new Yaml();
		InputStream inputStream = new FileInputStream(new File(FixedValues.resouce));
		Map<String, Object> data = yaml.load(inputStream);

		String processGroupId = (String) data.get(FixedValues.processGroupId);
		List<Map<String, Object>> processors = (List<Map<String, Object>>) data.get(FixedValues.processors);

		for (Map<String, Object> processor : processors) {
			String name = (String) processor.get(FixedValues.name);
			String type = (String) processor.get(FixedValues.type);
			int x = (int) ((Map<String, Integer>) processor.get(FixedValues.position)).get(FixedValues.xAxis);
			int y = (int) ((Map<String, Integer>) processor.get(FixedValues.position)).get(FixedValues.yAxis);


			//Calling Method For Creating Two Processors From Parent ProcessGroupId
			createProcessor(processGroupId, type, name, x, y, data);

		}
		return "Created";
	}

	private  void createProcessor(String processGroupId, String type, String name, int x, int y, Map<String, Object> data) throws Exception {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		String url = String.format("%s%s/processors", (String) data.get(FixedValues.finalUrl), processGroupId);

		String requestBody = String.format("{\"revision\":{\"version\":0},\"component\":{\"type\":\"%s\",\"name\":\"%s\",\"position\":{\"x\":%d,\"y\":%d}}}", type, name, x, y);

		HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
		ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
		if (exchange.getStatusCode() == HttpStatus.CREATED) {
			System.out.println("Processor created - Name: " + name + ", Type: " + type);

			String json = exchange.getBody(); // Get the JSON string from ResponseEntity
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(json); // Parse the JSON string into a JsonNode

			String id = rootNode.get("id").asText(); // Access the 'id' field as a String

			System.out.println("Processor ID: " + id);
			rootNode.get("component").get("name");
if(rootNode.get("component").get("name").asText().equals("ExecuteSQL Processor"))
{

	connectionDTO.setSourceProcessorId(id);
	connectionDTO.setProcessGroupId(processGroupId);
}
else {
	//ConnectionDTO connectionDTO = new ConnectionDTO();
	connectionDTO.setDestinationProcessorId(id);
	connectionDTO.setProcessGroupId(processGroupId);
}

			//startProcessor(id, data);


			//buildConnection(id, processGroupId, data);
		} else {
			System.out.println("Failed to create processor - Name: " + name + ", Type: " + type);
		}
	}



	//Run the Processor
	private static void startProcessor(String processorId, Map<String, Object> data) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		String url = String.format("%s%s/processors/%s/run-status", (String) data.get(FixedValues.finalUrl), FixedValues.processGroupId, processorId);

		String requestBody = "{\"revision\":{\"version\":0},\"state\":\"RUNNING\"}";

		HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
		restTemplate.put(url, requestEntity);
		System.out.println("Processor started - ID: " + processorId);
	}



	//Creating new Process Group In Apache Nifi LocalHost
	public Object creatingNewProcessGroup() throws Exception {


		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		InputStream inputStream = new FileInputStream(new File(FixedValues.resouce));

		Yaml yaml = new Yaml();
		Map<String, Object> data = yaml.load(inputStream);

		String processGroupName = (String) data.get("processGroupName");
		int xPosition = (int) data.get("xPosition");
		int yPosition = (int) data.get("yPosition");
		String urlPath = (String) data.get(FixedValues.finalUrl);

		String processGroupId = (String) data.get(FixedValues.processGroupId);

		processorId.put(FixedValues.parentId,processGroupId);


		String url = String.format("%s%s/process-groups", urlPath, processGroupId);
		String requestBody = String.format("{\"revision\":{\"version\":0},\"component\":{\"name\":\"%s\",\"position\":{\"x\":%d,\"y\":%d}}}", processGroupName, xPosition, yPosition);

		HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
		HttpStatusCode statusCode = responseEntity.getStatusCode();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());
		String id = jsonNode.get("component").get("id").asText();
		int statusCodeValue = responseEntity.getStatusCodeValue();
		if (statusCodeValue == 201) {
			System.out.println("Process group "+id+"created successfully.");

			newProcessGroupId = id;

			processorId.put(FixedValues.newProcessGroupId,newProcessGroupId);



			addingNewProcessGroupIdInApplicationApplicationPropertiesFile(newProcessGroupId);


			return id + " Is Created";
		} else {
			System.out.println("Failed to create process group. Status code: " + responseEntity.getStatusCodeValue());

			return processGroupName + " Is  Not Created";
		}


	}

	private static void addingNewProcessGroupIdInApplicationApplicationPropertiesFile(String newProcessGroupId) throws IOException {
		String filePath = "src/main/resources/application.properties";
		String newKey = "new_processgroup_id";


		// Load existing properties
		Properties properties = new Properties();
		try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
			properties.load(fileInputStream);
		}

		// Add new key-value pair
		properties.setProperty(newKey, newProcessGroupId);

		// Write updated properties back to the file
		try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
			properties.store(fileOutputStream, null);
		}

		System.out.println("Added new key-value pair to application.properties");
	}



	//Creating Processor Inside The New Process group
	public String createTwoProcessorsInNewProcessGroup() throws Exception {

		Yaml yaml = new Yaml();
		InputStream inputStream = new FileInputStream(new File(FixedValues.resouce));
		Map<String, Object> data = yaml.load(inputStream);


		List<Map<String, Object>> processors = (List<Map<String, Object>>) data.get(FixedValues.processors);

		for (Map<String, Object> processor : processors) {
			String name = (String) processor.get(FixedValues.name);
			String type = (String) processor.get(FixedValues.type);
			int x = (int) ((Map<String, Integer>) processor.get(FixedValues.position)).get(FixedValues.xAxis);
			int y = (int) ((Map<String, Integer>) processor.get(FixedValues.position)).get(FixedValues.yAxis);

			//createProcessor(processGroupId, type, name, x, y, data);

			Properties properties = new Properties();
			FileInputStream fis = new FileInputStream(FixedValues.resouceApplication) ;
				properties.load(fis);
				String newProcessGroupId = properties.getProperty("new_processgroup_id");
				System.out.println("New Process Group ID: " + newProcessGroupId);

			createProcessorForNewProcessGroup(newProcessGroupId, type, name, x, y, data);

		}
		return "Created";
	}


	private  void createProcessorForNewProcessGroup(String processGroupId, String type, String name, int x, int y, Map<String, Object> data) throws Exception {

		if(name.equalsIgnoreCase("InvokeHTTP Processor"))
		{

		}
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		String url = String.format("%s%s/processors", (String) data.get(FixedValues.finalUrl), processGroupId);

		String requestBody = String.format("{\"revision\":{\"version\":0},\"component\":{\"type\":\"%s\",\"name\":\"%s\",\"position\":{\"x\":%d,\"y\":%d}}}", type, name, x, y);

		if(name.equalsIgnoreCase("InvokeHTTP Processor"))
		{

		}
		HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
		ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
		if (exchange.getStatusCode() == HttpStatus.CREATED) {
			System.out.println("Processor created - Name: " + name + ", Type: " + type);

			String json = exchange.getBody(); // Get the JSON string from ResponseEntity
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(json); // Parse the JSON string into a JsonNode

			String id = rootNode.get("id").asText(); // Access the 'id' field as a String
			if(rootNode.get("component").get("name").asText().equals("InvokeHTTP Processor"))
			{
				processorId.put(FixedValues.sourceProcessorId,id);
				addingNewSourceIdInApplicationPropertiesFile(id);
			}
			else {
				processorId.put(FixedValues.sourceProcessorId,id);
addingNewDestinationeIdInApplicationPropertiesFile(id);
			}
			System.out.println("Processor ID: " + id);
			//startProcessor(id, data);


			//buildConnection(id, processGroupId, data);
		} else {
			System.out.println("Failed to create processor - Name: " + name + ", Type: " + type);
		}
	}

	private static void addingNewSourceIdInApplicationPropertiesFile(String sourceId) throws IOException {

		FileInputStream fileInputStream = new FileInputStream(FixedValues.resouceApplication);


		Properties properties = new Properties();


		properties.load(fileInputStream);

		properties.setProperty("sourceId",sourceId);


		FileOutputStream fileOutputStream = new FileOutputStream(FixedValues.resouceApplication);

		properties.store(fileOutputStream,"Added New SourceId in application.properties file");


		System.out.println("Added new key-value pair to application.properties");
	}


	private static void addingNewDestinationeIdInApplicationPropertiesFile(String destinationId) throws IOException {



		FileInputStream fileInputStream = new FileInputStream(FixedValues.resouceApplication);


		Properties properties = new Properties();


		properties.load(fileInputStream);

		properties.setProperty("destinationId",destinationId);


		FileOutputStream fileOutputStream = new FileOutputStream(FixedValues.resouceApplication);

		properties.store(fileOutputStream,"Added New DestinationId in application.properties file");


		System.out.println("Added new key-value pair to application.properties");
	}
}


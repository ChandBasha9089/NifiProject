package com.fisclouds.controller;

import com.fisclouds.connection.ConnectProcessors;
import com.fisclouds.entity.ConnectionDTO;
import com.fisclouds.service.NifiConfigureController;
import com.fisclouds.service.ProcessorTasks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NiFiConnection {


    @Autowired
    private ProcessorTasks processorTasks;



    @Autowired
    private NifiConfigureController nifiConfigureController;

    @Autowired
    private ConnectProcessors connectProcessors;


@PostMapping("/start")
    public Object configureServiceInProcessGroup() throws Exception {
        return nifiConfigureController.createDBCPConnectionPool();


    }





//
//        @GetMapping("/connect")
//        public Object connectProcessors() throws Exception {
//            return connectProcessors.connectTwoProcessors();
//
//
//
//        }


        @GetMapping("/getid")
    public Object getAllID()
        {
           return processorTasks.processorId;
        }



}

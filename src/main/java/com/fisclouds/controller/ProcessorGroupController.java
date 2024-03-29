package com.fisclouds.controller;

import com.fisclouds.service.ProcessorTasks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProcessorGroupController {


    @Autowired
    private ProcessorTasks processorTasks;



//    @PostMapping("/createprocessgroup")
//    public Object createNewProcesGroup() throws Exception {
//        return processorTasks.creatingNewProcessGroup();
//
//
//    }



}

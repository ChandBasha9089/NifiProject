package com.fisclouds.controller;

import com.fisclouds.service.ProcessorTasks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NifiProcessorController {


    @Autowired
    private ProcessorTasks processorTasks;


    @GetMapping("/twoprocessors")
    public Object creatingProcessors() throws Exception {
        String twoProcessors = processorTasks.createTwoProcessors();
        return twoProcessors;
    }





//    @PostMapping("/twonewprocessors")
//    public Object createNewProcesGroupInNewPGroup() throws Exception {
//
//        return processorTasks.createTwoProcessorsInNewProcessGroup();
//
//    }



}

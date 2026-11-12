package com.vgp.v1.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vgp.v1.dto.VectorsRequest;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/v1/vectors")
@AllArgsConstructor
public class VectorsController {

    @PostMapping 
    public String createVectors(@RequestBody VectorsRequest request){
        return "";
    }
}

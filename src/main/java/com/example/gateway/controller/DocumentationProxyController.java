package com.example.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static springfox.documentation.swagger2.web.Swagger2Controller.DEFAULT_URL;

@RestController
public class DocumentationProxyController {

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @GetMapping(DEFAULT_URL + "/{serviceName}")
    public Map proxy(@PathVariable("serviceName") String serviceName) {
        String documentationUri = getDocumentationUri(serviceName);
        RestTemplate restTemplate = restTemplateBuilder.build();
        return fixDocumentation(restTemplate.getForObject(documentationUri, Map.class));
    }

    private String getDocumentationUri(String serviceName) {
        ServiceInstance serviceInstance = loadBalancerClient.choose(serviceName);
        return serviceInstance.getUri().toString() + DEFAULT_URL;
    }

    @SuppressWarnings("unchecked")
    private Map fixDocumentation(Map documentation) {
        documentation.put("host", ""); // clean up original host
        return documentation;
    }

}

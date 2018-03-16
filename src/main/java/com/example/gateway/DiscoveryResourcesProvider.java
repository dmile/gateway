package com.example.gateway;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.List;
import java.util.stream.Collectors;

import static springfox.documentation.swagger2.web.Swagger2Controller.DEFAULT_URL;

@Component
@Primary
public class DiscoveryResourcesProvider implements SwaggerResourcesProvider {

    @Autowired
    private DiscoveryClient discoveryClient;

//    @Autowired
//    private ZuulProperties zuulProperties;

    @Override
    public List<SwaggerResource> get() {
        return discoveryClient.getServices().stream()
                .sorted()
                .map(this::asSwaggerResource)
                .collect(Collectors.toList());
    }

    private SwaggerResource asSwaggerResource(String serviceName) {
        String location = UriComponentsBuilder.fromPath(DEFAULT_URL)
                .pathSegment(serviceName)
                .build()
                .toUriString();
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(serviceName);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion("2.0");
        return swaggerResource;
    }

}

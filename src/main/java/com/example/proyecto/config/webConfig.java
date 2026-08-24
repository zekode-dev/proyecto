package com.example.proyecto.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

@Configuration
public class webConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // =====================================================
        // IMÁGENES DINÁMICAS
        // =====================================================

        registry.addResourceHandler("/img/**")
                .addResourceLocations("file:img/")
                .setCacheControl(
                        CacheControl
                                .maxAge(1, TimeUnit.SECONDS)
                                .cachePublic()
                );

    }

}
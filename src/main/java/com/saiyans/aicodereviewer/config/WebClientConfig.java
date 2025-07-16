package com.saiyans.aicodereviewer.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
    	try {
            SslContext sslContext = SslContextBuilder.forClient()
                .protocols("TLSv1.2")
                .trustManager(InsecureTrustManagerFactory.INSTANCE) // Replace in prod!
                .build();

            HttpClient httpClient = HttpClient.newConnection()
                .secure(spec -> spec.sslContext(sslContext))
                .compress(true)
                .wiretap(true);

            return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize WebClient with SSL", e);
        }

    }
}
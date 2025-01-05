package org.example.clientservice;

import org.example.clientservice.entity.Client;
import org.example.clientservice.repository.ClientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;

@EnableFeignClients
@SpringBootApplication
@EnableDiscoveryClient
public class ClientServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClientServiceApplication.class, args);
    }

    // Insérer des données de test au démarrage
    @Bean
    CommandLineRunner initDatabase(ClientRepository clientRepository) {
        return args -> {
            clientRepository.save(new Client("John Doe", "john.doe@example.com"));
            clientRepository.save(new Client("Jane Smith", "jane.smith@example.com"));
            clientRepository.save(new Client("Mike Brown", "mike.brown@example.com"));
        };
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }
}

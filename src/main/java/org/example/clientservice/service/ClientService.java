package org.example.clientservice.service;

import org.example.clientservice.entity.Client;
import org.example.clientservice.repository.ClientRepository;
import org.example.voitureservice.entity.Voiture;
import org.example.clientservice.client.VoitureFeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Arrays;
import org.springframework.core.ParameterizedTypeReference;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.micrometer.core.instrument.Timer;

@Service
public class ClientService {
    private static final Logger logger = LoggerFactory.getLogger(ClientService.class);
    private final ClientRepository clientRepository;
    private final RestTemplate restTemplate;
    private final VoitureFeignClient voitureFeignClient;
    private final WebClient.Builder webClientBuilder;
    private final MeterRegistry meterRegistry;

    public ClientService(ClientRepository clientRepository, RestTemplate restTemplate, 
            VoitureFeignClient voitureFeignClient, WebClient.Builder webClientBuilder, 
            MeterRegistry meterRegistry) {
        this.clientRepository = clientRepository;
        this.restTemplate = restTemplate;
        this.voitureFeignClient = voitureFeignClient;
        this.webClientBuilder = webClientBuilder;
        this.meterRegistry = meterRegistry;
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Client addClient(Client client) {
        return clientRepository.save(client);
    }

    public Client getClientById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + id));
    }

    public List<Voiture> getVoituresWithRestTemplate() {
        return restTemplate.getForObject("http://voiture-service/api/voitures", List.class);
    }

    public List<Voiture> getVoituresWithFeign() {
        return voitureFeignClient.getVoitures();
    }

    public List<Voiture> getVoituresWithWebClient() {
        return webClientBuilder.build()
                .get()
                .uri("http://voiture-service/api/voitures")
                .retrieve()
                .bodyToFlux(Voiture.class)
                .collectList()
                .block();
    }

    // Endpoints for testing each implementation
    @Timed(value = "rest.template.request", description = "Time taken for RestTemplate request")
    public List<Voiture> getAllVoituresRestTemplate() {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            List<Voiture> voitures = Arrays.asList(restTemplate.getForObject("http://voiture-service/api/voitures", Voiture[].class));
            sample.stop(meterRegistry.timer("rest.template.execution.time"));
            meterRegistry.counter("rest.template.calls").increment();
            return voitures;
        } catch (Exception e) {
            meterRegistry.counter("rest.template.errors").increment();
            throw e;
        }
    }

    @Timed(value = "feign.request", description = "Time taken for Feign request")
    public List<Voiture> getAllVoituresFeign() {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            List<Voiture> voitures = voitureFeignClient.findAll();
            sample.stop(meterRegistry.timer("feign.execution.time"));
            meterRegistry.counter("feign.calls").increment();
            return voitures;
        } catch (Exception e) {
            meterRegistry.counter("feign.errors").increment();
            throw e;
        }
    }

    @Timed(value = "webclient.request", description = "Time taken for WebClient request")
    public List<Voiture> getAllVoituresWebClient() {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            List<Voiture> voitures = webClientBuilder.build()
                    .get()
                    .uri("http://voiture-service/api/voitures")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Voiture>>() {})
                    .block();
            sample.stop(meterRegistry.timer("webclient.execution.time"));
            meterRegistry.counter("webclient.calls").increment();
            return voitures;
        } catch (Exception e) {
            meterRegistry.counter("webclient.errors").increment();
            throw e;
        }
    }
}

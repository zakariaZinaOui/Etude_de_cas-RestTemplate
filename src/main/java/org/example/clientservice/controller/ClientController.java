package org.example.clientservice.controller;

import org.example.clientservice.entity.Client;
import org.example.clientservice.service.ClientService;
import org.example.voitureservice.entity.Voiture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ClientController {
    @Autowired
    private ClientService clientService;

    // Client endpoints
    @GetMapping("/api/client")
    public List<Client> getAllClients() {
        return clientService.getAllClients();
    }

    @GetMapping("/api/client/{id}")
    public Client getClientById(@PathVariable Long id) {
        return clientService.getClientById(id);
    }

    @PostMapping("/api/client")
    public Client addClient(@RequestBody Client client) {
        return clientService.addClient(client);
    }

    // Performance test endpoints
    @GetMapping("/test-performance/rest-template")
    public List<Voiture> testRestTemplate() {
        return clientService.getAllVoituresRestTemplate();
    }

    @GetMapping("/test-performance/feign")
    public List<Voiture> testFeign() {
        return clientService.getAllVoituresFeign();
    }

    @GetMapping("/test-performance/webclient")
    public List<Voiture> testWebClient() {
        return clientService.getAllVoituresWebClient();
    }

    // Original voiture endpoints
    @GetMapping("/api/client/voitures/resttemplate")
    public List<Voiture> getVoituresRestTemplate() {
        return clientService.getVoituresWithRestTemplate();
    }

    @GetMapping("/api/client/voitures/feign")
    public List<Voiture> getVoituresWithFeign() {
        return clientService.getVoituresWithFeign();
    }

    @GetMapping("/api/client/voitures/webclient")
    public List<Voiture> getVoituresWebClient() {
        return clientService.getVoituresWithWebClient();
    }
}

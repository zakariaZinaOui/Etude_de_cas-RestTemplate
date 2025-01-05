package org.example.clientservice.client;

import org.example.voitureservice.entity.Voiture;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "voiture-service")
public interface VoitureFeignClient {
    @GetMapping("/api/voitures")
    List<Voiture> findAll();

    @GetMapping("/api/voitures")
    List<Voiture> getVoitures();
}

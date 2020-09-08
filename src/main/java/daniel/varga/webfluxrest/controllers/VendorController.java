package daniel.varga.webfluxrest.controllers;

import daniel.varga.webfluxrest.domain.Vendor;
import daniel.varga.webfluxrest.repositories.VendorRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class VendorController {
    public static final String BASE_URL = "/api/v1/vendors";
    private final VendorRepository vendorRepository;

    public VendorController(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    @GetMapping(BASE_URL)
    public Flux<Vendor> listVendors() {
        return vendorRepository.findAll();
    }

    @GetMapping(BASE_URL + "/{id}")
    public Mono<Vendor> getVendorById(@PathVariable String id) {
        return vendorRepository.findById(id);
    }
}

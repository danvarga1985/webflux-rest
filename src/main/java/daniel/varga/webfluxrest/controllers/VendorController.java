package daniel.varga.webfluxrest.controllers;

import daniel.varga.webfluxrest.domain.Vendor;
import daniel.varga.webfluxrest.repositories.VendorRepository;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.Flow;

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

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(BASE_URL)
    Mono<Void> createVendor(@RequestBody Publisher<Vendor> vendorStream) {
        return vendorRepository.saveAll(vendorStream).then();
    }

    @PutMapping(BASE_URL + "/{id}")
    Mono<Vendor> updateVendor(@PathVariable String id, @RequestBody Vendor vendor) {
        vendor.setId(id);
        return vendorRepository.save(vendor);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping(BASE_URL + "/{id}")
    Mono<Vendor> patchVendor(@PathVariable String id, @RequestBody Vendor vendor) {
        Mono<Vendor> foundVendor = vendorRepository.findById(id);

        return foundVendor
                .filter(found -> !found.getFirstName().equals(vendor.getFirstName())
                        || !found.getLastName().equals(vendor.getLastName()))
                .flatMap(f -> {
                    if (!f.getFirstName().equals(vendor.getFirstName())) {
                        f.setFirstName(f.getFirstName());
                        f.setFirstName(vendor.getFirstName());
                    }
                    if (!f.getLastName().equals(vendor.getLastName())) {
                        f.setLastName(f.getLastName());
                        f.setLastName(vendor.getLastName());
                    }
//                    f.setFirstName(vendor.getFirstName());
//                    f.setLastName(vendor.getLastName());
                    return vendorRepository.save(f);
                }).switchIfEmpty(foundVendor);
    }

}

package daniel.varga.webfluxrest.controllers;

import daniel.varga.webfluxrest.domain.Category;
import daniel.varga.webfluxrest.domain.Vendor;
import daniel.varga.webfluxrest.repositories.VendorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class VendorControllerTest {
    WebTestClient webTestClient;
    VendorController vendorController;
    VendorRepository vendorRepository;

    @BeforeEach
    void setUp() {
        vendorRepository = Mockito.mock(VendorRepository.class);
        vendorController = new VendorController(vendorRepository);
        webTestClient = WebTestClient.bindToController(vendorController).build();
    }

    @Test
    void testListVendors() {
        given(vendorRepository.findAll())
                .willReturn(Flux.just(Vendor.builder().firstName("Brother").lastName("Theodore").build(),
                        Vendor.builder().firstName("Cliff").lastName("Barnes").build()));

        webTestClient.get()
                .uri(VendorController.BASE_URL)
                .exchange()
                .expectBodyList(Vendor.class)
                .hasSize(2);
    }

    @Test
    void testGetVendorById() {
        Vendor vendor = Vendor.builder().firstName("Tom").lastName("Waits").id(String.valueOf(1)).build();

        given(vendorRepository.findById("1"))
                .willReturn(Mono.just(vendor));

        webTestClient.get()
                .uri(VendorController.BASE_URL + "/1")
                .exchange()
                .expectBody(Vendor.class)
                .isEqualTo(vendor);
    }

    @Test
    void testCreateVendor() {
        given(vendorRepository.saveAll(any(Publisher.class)))
                .willReturn(Flux.just(Vendor.builder().build()));

        Mono<Vendor> vendorToSaveMono = Mono.just(Vendor.builder().firstName("Ernst").lastName("Junger").build());

        webTestClient.post()
                .uri(VendorController.BASE_URL)
                .body(vendorToSaveMono, Vendor.class)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void testUpdateVendor() {
        given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(Vendor.builder().build()));

        Mono<Vendor> vendorToUpdate = Mono.just(Vendor.builder().firstName("Ernst").lastName("Junger").build());

        webTestClient.put()
                .uri(VendorController.BASE_URL + "/1")
                .body(vendorToUpdate, Vendor.class)
                .exchange()
                .expectStatus().isOk();
    }
}
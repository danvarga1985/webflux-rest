package daniel.varga.webfluxrest.controllers;

import daniel.varga.webfluxrest.domain.Vendor;
import daniel.varga.webfluxrest.repositories.VendorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

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
    void listVendors() {
        BDDMockito.given(vendorRepository.findAll())
                .willReturn(Flux.just(Vendor.builder().firstName("Brother").lastName("Theodore").build(),
                        Vendor.builder().firstName("Cliff").lastName("Barnes").build()));

        webTestClient.get()
                .uri(VendorController.BASE_URL)
                .exchange()
                .expectBodyList(Vendor.class)
                .hasSize(2);
    }

    @Test
    void getVendorById() {
        Vendor vendor = Vendor.builder().firstName("Tom").lastName("Waits").id(String.valueOf(1)).build();

        BDDMockito.given(vendorRepository.findById("1"))
                .willReturn(Mono.just(vendor));

        webTestClient.get()
                .uri(VendorController.BASE_URL + "/1")
                .exchange()
                .expectBody(Vendor.class)
                .isEqualTo(vendor);
    }
}
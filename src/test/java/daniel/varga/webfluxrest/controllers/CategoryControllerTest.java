package daniel.varga.webfluxrest.controllers;

import daniel.varga.webfluxrest.domain.Category;
import daniel.varga.webfluxrest.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.Flow;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

class CategoryControllerTest {

    WebTestClient webTestClient;
    CategoryRepository categoryRepository;
    CategoryController categoryController;

    @BeforeEach
    void setUp() {
        categoryRepository = Mockito.mock(CategoryRepository.class);
        categoryController = new CategoryController(categoryRepository);
        webTestClient = WebTestClient.bindToController(categoryController).build();
    }

    @Test
    void listCategories() {
        BDDMockito.given(categoryRepository.findAll())
                .willReturn(Flux.just(Category.builder().description("Cat1").build(),
                        Category.builder().description("Cat2").build()));

        webTestClient.get()
                .uri(CategoryController.BASE_URL)
                .exchange()
                .expectBodyList(Category.class)
                .hasSize(2);
    }

    @Test
    void getCategoryById() {
        Category category = Category.builder().description("Wet").id(String.valueOf(1)).build();

        BDDMockito.given(categoryRepository.findById("1"))
                .willReturn(Mono.just(category));

        webTestClient.get()
                .uri(CategoryController.BASE_URL + "/1")
                .exchange()
                .expectBody(Category.class)
                .isEqualTo(category);

    }

    @Test
    void testCreateCategory() {
        BDDMockito.given(categoryRepository.saveAll(any(Publisher.class)))
                .willReturn(Flux.just(Category.builder().build()));

        Mono<Category> categoryToSaveMono = Mono.just(Category.builder().description("whatevs").build());

        webTestClient.post()
                .uri(CategoryController.BASE_URL)
                .body(categoryToSaveMono, Category.class)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void testUpdateCategory() {
        BDDMockito.given(categoryRepository.save(any(Category.class)))
                .willReturn(Mono.just(Category.builder().build()));

        Mono<Category> categoryToUpdate = Mono.just(Category.builder().description("Something").build());

        webTestClient.put()
                .uri(CategoryController.BASE_URL + "/1")
                .body(categoryToUpdate, Category.class)
                .exchange()
                .expectStatus().isOk();
    }
}
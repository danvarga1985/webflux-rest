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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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
        given(categoryRepository.findAll())
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

        given(categoryRepository.findById("1"))
                .willReturn(Mono.just(category));

        webTestClient.get()
                .uri(CategoryController.BASE_URL + "/1")
                .exchange()
                .expectBody(Category.class)
                .isEqualTo(category);

    }

    @Test
    void testCreateCategory() {
        given(categoryRepository.saveAll(any(Publisher.class)))
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
        given(categoryRepository.save(any(Category.class)))
                .willReturn(Mono.just(Category.builder().build()));

        Mono<Category> categoryToUpdate = Mono.just(Category.builder().description("Something").build());

        webTestClient.put()
                .uri(CategoryController.BASE_URL + "/1")
                .body(categoryToUpdate, Category.class)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void testPatchCategoryWithChanges() {
        given(categoryRepository.findById(anyString()))
                .willReturn(Mono.just(Category.builder().description("test").build()));

        given(categoryRepository.save(any(Category.class)))
                .willReturn(Mono.just(Category.builder().description("test").build()));

        Mono<Category> catToUpdateMono = Mono.just(Category.builder().description("New Description").build());

        webTestClient.patch()
                .uri(CategoryController.BASE_URL + "/asdfasdf")
                .body(catToUpdateMono, Category.class)
                .exchange()
                .expectStatus()
                .isOk();

        verify(categoryRepository).save(any());
    }

    @Test
    public void testPatchCategoryNoChanges() {
        given(categoryRepository.findById(anyString()))
                .willReturn(Mono.just(Category.builder().description("test").build()));

        given(categoryRepository.save(any(Category.class)))
                .willReturn(Mono.just(Category.builder().description("test").build()));

        Mono<Category> catToUpdateMono = Mono.just(Category.builder().description("test").build());

        webTestClient.patch()
                .uri(CategoryController.BASE_URL +"/1")
                .body(catToUpdateMono, Category.class)
                .exchange()
                .expectStatus()
                .isOk();

        verify(categoryRepository, never()).save(any());
    }

}
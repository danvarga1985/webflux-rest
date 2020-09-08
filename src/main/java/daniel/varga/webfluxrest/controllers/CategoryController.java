package daniel.varga.webfluxrest.controllers;

import daniel.varga.webfluxrest.domain.Category;
import daniel.varga.webfluxrest.repositories.CategoryRepository;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RestController
public class CategoryController {
    public static final String BASE_URL = "/api/v1/categories";
    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping(BASE_URL)
    public Flux<Category> listCategories() {
        return categoryRepository.findAll();
    }

    @GetMapping(BASE_URL + "/{id}")
    public Mono<Category> getCategoryById(@PathVariable String id) {
        return categoryRepository.findById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(BASE_URL)
    Mono<Void> createCategory(@RequestBody Publisher<Category> categoryStream) {
        return categoryRepository.saveAll(categoryStream).then();
    }

    @PutMapping(BASE_URL + "/{id}")
    Mono<Category> updateCategory(@PathVariable String id, @RequestBody Category category) {
        category.setId(id);
        return categoryRepository.save(category);
    }

    @PatchMapping(BASE_URL + "/{id}")
    Mono<Category> patchCategory(@PathVariable String id, @RequestBody Category category) {
        Mono<Category> foundCategory = categoryRepository.findById(id);

        return foundCategory
                .filter(found -> !found.getDescription().equals(category.getDescription()))
                .flatMap(f -> {
                    f.setDescription(category.getDescription());

                    return categoryRepository.save(f);
                }).switchIfEmpty(foundCategory);
    }
}

package daniel.varga.webfluxrest.bootstrap;

import daniel.varga.webfluxrest.domain.Category;
import daniel.varga.webfluxrest.domain.Vendor;
import daniel.varga.webfluxrest.repositories.CategoryRepository;
import daniel.varga.webfluxrest.repositories.VendorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Bootstrap implements CommandLineRunner {
    private final VendorRepository vendorRepository;
    private final CategoryRepository categoryRepository;

    public Bootstrap(VendorRepository vendorRepository, CategoryRepository categoryRepository) {
        this.vendorRepository = vendorRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (categoryRepository.count().block() == 0) {
            log.debug("Loading Category Data");
            loadCategories();
            log.debug("Loading Vendor Data");
            loadVendors();

            System.out.println("Categories: " + categoryRepository.count().block());
            System.out.println("Vendors: " + vendorRepository.count().block());
        }
    }

    private void loadVendors() {
        Vendor vendor1 = Vendor.builder().firstName("Ernst").lastName("Junger").build();
        vendorRepository.save(vendor1).block();

        Vendor vendor2 = Vendor.builder().firstName("Knut").lastName("Hamsun").build();
        vendorRepository.save(vendor2).block();

        vendorRepository.save(Vendor.builder().firstName("John").lastName("Steinbeck").build()).block();

    }

    private void loadCategories() {
        Category category1 = Category.builder().description("Dried").build();
        categoryRepository.save(category1).block();

        Category category2 = Category.builder().description("Exotic").build();
        categoryRepository.save(category2).block();

        Category category3 = categoryRepository.save(Category.builder().description("Wet").build()).block();
    }

}

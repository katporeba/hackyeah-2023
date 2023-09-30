package com.hackyeah.sl.backend.utilty;

import com.hackyeah.sl.backend.domain.Category;
import com.hackyeah.sl.backend.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class InitCategories implements CommandLineRunner {
    private CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        //initCategories();
    }

    private void initCategories() {
        Category dog = Category.builder()
                .name("Pies")
                .description("Jak się zachowywać Nigdy nie zbliżaj się do psa frontalnie, podchodź do niego lekko z boku. Zachowaj spokój i opanowanie. W przypadku, jeżeli się boisz lub denerwujesz sytuacją, lepiej nie zbliżaj się do zwierzęcia. Twoje własne bezpieczeństwo należy w tym wypadku przedłożyć nad miłość do zwierząt.")
                .build();

        Category cat = Category.builder()
                .name("Kot")
                .description("Nie zbliżaj się zbyt blisko: Dzikie koty są zazwyczaj przestraszone ludzi i mogą się bronić, gdy poczują się zagrożone. Zawsze zachowuj pewną odległość i nie próbuj dotykać dzikiego kota.")
                .build();

        Category lemur = Category.builder()
                .name("Lemur")
                .description("Nie karm dzikiego lemura: Nie próbuj karmić dzikiego lemura. Ich dieta składa się z naturalnych pożywienia dostępnego w ich środowisku, a karmienie ich jedzeniem może zakłócić ich zdolność do zdobywania pokarmu w dzikiej przyrodzie.")
                .build();

        Category teddybear = Category.builder()
                .name("Niedzwiedz")
                .description("Jak się zachowywaćPodczas bliskiego spotkania nie patrz drapieżnikowi w oczy (dla zwierząt to sygnał agresji). Jeśli niedźwiedź się zbliża, pozostań na swoim miejscu i odzywaj się spokojnym głosem. Jeśli się zatrzyma – zwiększ dystans wycofując się powoli. Postaraj się zejść mu z drogi.")
                .build();


        Category boar = Category.builder()
                .name("Dzik")
                .description("Kluczowe są zachowanie spokoju i brak gwałtownych ruchów. Dzik atakuje, gdy czuje się zagrożony, dlatego najlepiej pozostać w bezruchu i poczekać, aż osobnik się oddali.")
                .build();


        categoryRepository.saveAll(List.of(dog, cat, lemur, teddybear, boar));
    }
}

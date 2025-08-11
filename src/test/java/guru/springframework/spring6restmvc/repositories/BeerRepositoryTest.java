package guru.springframework.spring6restmvc.repositories;

import guru.springframework.spring6restmvc.bootstrap.BootstrapData;
import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.model.BeerCSVRecord;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.service.BeerCsvService;
import guru.springframework.spring6restmvc.service.BeerCsvServiceImpl;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;


import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({BootstrapData.class, BeerCsvServiceImpl.class})
class BeerRepositoryTest {

    @Autowired
    private BeerRepository beerRepository;

    @Test
    void testSaveBeer(){
        Beer savedBeer = beerRepository.save(Beer.builder()
                        .beerName("BeerJPA")
                        .beerStyle(BeerStyle.BLANCHE)
                        .upc("12345")
                        .price(new BigDecimal("12.99"))
                .build());

        beerRepository.flush();

        assertNotNull(savedBeer);
        assertNotNull(savedBeer.getId());
    }

    @Test
    void testSaveBeerNameTooLong(){

        assertThrows(ConstraintViolationException.class, () ->{
            Beer savedBeer = beerRepository.save(Beer.builder()
                .beerName("BeerJPA22131231231231231231231231231242342134132512343124134514324134513241234124321241234")
                .beerStyle(BeerStyle.BLANCHE)
                .upc("12345")
                .price(new BigDecimal("12.99"))
                .build());

            beerRepository.flush();

            assertNotNull(savedBeer);
            assertNotNull(savedBeer.getId());});


    }

    @Test
    void testGetBeerListByName() {
        List<Beer> list =  beerRepository.findAllByBeerNameIsLikeIgnoreCase("%IPA%");

        assertThat(list.size()).isEqualTo(336);
    }

    @Test
    void testGetBeerListByStyle() {
        List<Beer> list = beerRepository.findAllByBeerStyle(BeerStyle.SAISON);

        assertThat(list.size()).isEqualTo(52);
    }
}
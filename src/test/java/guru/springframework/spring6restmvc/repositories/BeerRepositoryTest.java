package guru.springframework.spring6restmvc.repositories;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.model.BeerStyle;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
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
}
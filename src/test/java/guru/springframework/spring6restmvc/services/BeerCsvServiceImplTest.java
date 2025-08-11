package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.model.BeerCSVRecord;
import guru.springframework.spring6restmvc.service.BeerCsvService;
import guru.springframework.spring6restmvc.service.BeerCsvServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BeerCsvServiceImplTest {

    BeerCsvService beerCsvService = new BeerCsvServiceImpl();

    @Test
    void convertCSV() throws FileNotFoundException {

        File file = ResourceUtils.getFile("classpath:csvdata/beers.csv");

        List<BeerCSVRecord> recs = beerCsvService.convertCSV(file);

        System.out.println(recs.size());

        assertThat(recs.size()).isGreaterThan(0);
    }
}

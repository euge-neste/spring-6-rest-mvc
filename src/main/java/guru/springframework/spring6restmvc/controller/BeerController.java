package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.service.BeerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
public class BeerController {
    private final BeerService beerService;

    public static final String BEER_PATH = "/api/v1/beer";
    public static final String BEER_ID_PATH = "/api/v1/beer/{beerId}";

    @GetMapping(BEER_PATH)
    public List<BeerDTO> listBeers() {
        return beerService.listBeers();
    }

    @GetMapping(BEER_ID_PATH)
    public BeerDTO getBeerById(@PathVariable("beerId") UUID beerId) {

        log.debug("Controller - Getting Beer by id - {}", beerId);

        return beerService.getBeerById(beerId).orElseThrow(NotFoundException::new);
    }

    @PostMapping(BEER_PATH)
    public ResponseEntity handlePost(@Validated @RequestBody BeerDTO beer) {

        BeerDTO savedBeer = beerService.saveNewBeer(beer);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, BEER_PATH + "/" + savedBeer.getId());

        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    @PutMapping(BEER_ID_PATH)
    public ResponseEntity updateById(@Validated @RequestBody BeerDTO beer, @PathVariable("beerId") UUID beerId) {

        if(beerService.updateBeerById(beerId, beer).isEmpty()){
            throw new NotFoundException();
        }

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(BEER_ID_PATH)
    public ResponseEntity deleteById(@PathVariable("beerId") UUID beerId) {

        if (!beerService.deleteBeerById(beerId)){
            throw new NotFoundException();
        }

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(BEER_ID_PATH)
    public ResponseEntity handlePatch(@PathVariable("beerId") UUID beerId, @RequestBody BeerDTO beer) {

        beerService.patchBeerById(beerId, beer);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity handleNotFound(){
        return ResponseEntity.notFound().build();
    }
}

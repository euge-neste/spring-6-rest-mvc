package guru.springframework.spring6restmvc.service;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class BeerServiceJPA implements BeerService {

    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;

    @Override
    public List<BeerDTO> listBeers(String beerName, BeerStyle beerStyle, Boolean showInventory) {

        List<Beer> beerList;

        if (StringUtils.hasText(beerName) && beerStyle == null) {
            beerList = listBeersByName(beerName);
        } else if (!StringUtils.hasText(beerName) && beerStyle != null) {
            beerList = listBeersByStyle(beerStyle);
        } else if (StringUtils.hasText(beerName) && beerStyle != null){
            beerList = listBeersByNameAndStyle(beerName, beerStyle);
        } else {
            beerList = beerRepository.findAll();
        }

        if (showInventory!=null && !showInventory) {
            beerList.forEach(beer -> beer.setQuantityOnHand(null));
        }

        return beerList.stream()
                .map(beerMapper::beerToBeerDto)
                .collect(Collectors.toList());
    }

    private List<Beer> listBeersByNameAndStyle(String beerName, BeerStyle beerStyle) {
        return beerRepository.findAllByBeerNameIsLikeIgnoreCaseAndBeerStyle("%" + beerName + "%", beerStyle);
    }

    List<Beer> listBeersByStyle(BeerStyle beerStyle) {
        return beerRepository.findAllByBeerStyle(beerStyle);
    }

    List<Beer> listBeersByName(String beerName) {
        return beerRepository.findAllByBeerNameIsLikeIgnoreCase("%" + beerName + "%");
    }

    @Override
    public Optional<BeerDTO> getBeerById(UUID id) {
        return Optional.ofNullable(beerMapper.beerToBeerDto(beerRepository.findById(id).orElse(null)));
    }

    @Override
    public BeerDTO saveNewBeer(BeerDTO beer) {
        return beerMapper.beerToBeerDto(beerRepository.save(beerMapper.beerDtoToBeer(beer)));
    }

    @Override
    public Optional<BeerDTO> updateBeerById(UUID beerId, BeerDTO beer) {

        AtomicReference<Optional<BeerDTO>> atomicBeerDTO = new AtomicReference<>();

        beerRepository.findById(beerId).ifPresentOrElse(foundBeer -> {
            foundBeer.setBeerName(beer.getBeerName());
            foundBeer.setBeerStyle(beer.getBeerStyle());
            foundBeer.setPrice(beer.getPrice());
            foundBeer.setQuantityOnHand(beer.getQuantityOnHand());
            foundBeer.setUpc(beer.getUpc());
            foundBeer.setUpdatedDate(LocalDateTime.now());

            atomicBeerDTO.set(Optional.of(beerMapper.beerToBeerDto(beerRepository.save(foundBeer))));
        }, () -> {
            atomicBeerDTO.set(Optional.empty());
        });

        return atomicBeerDTO.get();
    }

    @Override
    public Boolean deleteBeerById(UUID beerId) {
        if (beerRepository.existsById(beerId)) {
            beerRepository.deleteById(beerId);
            return true;
        }
        return false;
    }

    @Override
    public Optional<BeerDTO> patchBeerById(UUID beerId, BeerDTO beer) {

        AtomicReference<Optional<BeerDTO>> atomicBeerDTO = new AtomicReference<>();

        beerRepository.findById(beerId).ifPresentOrElse(foundBeer -> {

            if (StringUtils.hasText(beer.getBeerName())) {
                foundBeer.setBeerName(beer.getBeerName());
            }

            if (beer.getBeerStyle() != null) {
                foundBeer.setBeerStyle(beer.getBeerStyle());
            }

            if (beer.getPrice() != null) {
                foundBeer.setPrice(beer.getPrice());
            }

            if (beer.getQuantityOnHand() != null) {
                foundBeer.setQuantityOnHand(beer.getQuantityOnHand());
            }

            if (StringUtils.hasText(beer.getUpc())) {
                foundBeer.setUpc(beer.getUpc());
            }

            log.info(foundBeer.getUpc());

            atomicBeerDTO.set(Optional.of(beerMapper.beerToBeerDto(beerRepository.save(foundBeer))));

        }, () -> {
            atomicBeerDTO.set(Optional.empty());
        });

        return atomicBeerDTO.get();
    }
}

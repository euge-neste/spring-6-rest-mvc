package guru.springframework.spring6restmvc.mappers;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.model.BeerDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BeerMapper {

    Beer beerDtoToBeer(BeerDTO beerDTO);

    BeerDTO beerToBeerDto(Beer beer);

}

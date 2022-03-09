package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.CityEntity;
import fr.tobby.tripnjoyback.repository.CityRepository;
import org.springframework.stereotype.Service;

@Service
public class CityService {

    private final CityRepository cityRepository;

    public CityService(final CityRepository cityRepository)
    {
        this.cityRepository = cityRepository;
    }

    public CityEntity getOrAddCity(final String name)
    {
        return cityRepository.findByName(name)
                             .orElseGet(() -> cityRepository.save(new CityEntity(name)));
    }
}

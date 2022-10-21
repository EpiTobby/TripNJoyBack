package fr.tripnjoy.users.service;

import fr.tripnjoy.users.entity.CityEntity;
import fr.tripnjoy.users.repository.CityRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CityService {

    private final CityRepository cityRepository;

    public CityService(final CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @NonNull
    @Transactional
    public CityEntity getOrAddCity(final String name) {
        return cityRepository.findByName(name)
                .orElseGet(() -> cityRepository.save(new CityEntity(name)));
    }
}

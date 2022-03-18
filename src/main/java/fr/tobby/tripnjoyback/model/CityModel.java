package fr.tobby.tripnjoyback.model;

import fr.tobby.tripnjoyback.entity.CityEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CityModel {

    private String name;

    public static CityModel of(final CityEntity entity)
    {
        if (entity == null)
            return null;
        return new CityModel(entity.getName());
    }
}

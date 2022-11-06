package fr.tripnjoy.places.model;

import lombok.Getter;

@Getter
public enum PlaceCategory {
    ANTIQUES_SHOP("commercial.antiques"),
    ARTS_CENTER("entertainment.culture.arts_centre"),
    ART_SHOP("commercial.art"),
    BAR_AND_PUB("catering.pub,catering.bar"),
    BEACH("beach"),
    BUS("public_transport.bus"),
    CHANGE("service.financial.bureau_de_change"),
    CHINESE_RESTAURANT("catering.restaurant.chinese"),
    COFFEE_SHOP("catering.cafe.coffee_shop"),
    ENTERTAINMENT("entertainment"),
    FAST_FOOD("catering.fast_food"),
    FISH_AND_CHIPS_RESTAURANT("catering.restaurant.fish_and_chips"),
    ITALIAN_RESTAURANT("catering.restaurant.italian"),
    MUSEUM("entertainment.museum"),
    PARKING("parking"),
    RESTAURANT("catering.restaurant"),
    SEAFOOD_RESTAURANT("catering.restaurant.seafood"),
    SUPERMARKET("commercial.supermarket"),
    SWIMMING_POOL("sport.swimming_pool"),
    SUBWAY("public_transport.subway"),
    THEATRE("entertainment.culture.theatre"),
    TOURISM("tourism"),
    TRANSPORT("public_transport");

    protected final String categoryValue;

    PlaceCategory(String categoryValue) {
        this.categoryValue = categoryValue;
    }
}

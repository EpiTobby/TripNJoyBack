package fr.tobby.tripnjoyback.model;

public enum PlaceType {
    ANTIQUES_SHOP("commercial.antiques"),
    ARTS_CENTER("entertainment.culture.arts_centre"),
    ART_SHOP("commercial.art"),
    BAR_AND_PUB("catering.pub,catering.bar"),
    CHINESE_RESTAURANT("catering.restaurant.chinese"),
    COFEE_SHOP("catering.cafe.coffee_shop"),
    ENTERTAINEMENT("entertainment"),
    FAST_FOOD("catering.fast_food"),
    FISH_AND_CHIPS_RESTAURANT("catering.restaurant.fish_and_chips"),
    ITALIAN_RESTAURANT("catering.restaurant.italian"),
    MUSEUM("entertainment.museum"),
    PARKING("parking"),
    RESTAURANT("catering.restaurant"),
    SEAFOOD_RESTARANT("catering.restaurant.seafood"),
    SUPERMARKET("commercial.supermarket"),
    THEATRE("entertainment.culture.theatre"),
    TOURISM("tourism");

    protected final String categoryValue;

    PlaceType(String categoryValue) {
        this.categoryValue = categoryValue;
    }
}

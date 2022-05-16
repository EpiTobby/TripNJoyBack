package fr.tobby.tripnjoyback.utils;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ColorUtils {

    private ColorUtils()
    {
    }

    public static String colorToString(@NotNull final Color color)
    {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
}

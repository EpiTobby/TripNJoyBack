package fr.tobby.tripnjoyback.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

public final class DateUtils {

    private DateUtils()
    {
    }

    @NotNull
    public static Date min(@NotNull final Date a, @NotNull final Date b)
    {
        return a.before(b) ? a : b;
    }

    @NotNull
    public static Date max(@NotNull final Date a, @NotNull final Date b)
    {
        return a.before(b) ? b : a;
    }
}

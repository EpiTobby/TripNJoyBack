package fr.tripnjoy.users.model;

public enum UserRole {
    DEFAULT("default"),
    ADMIN("admin"),
    ;

    private final String name;

    UserRole(final String name)
    {
        this.name = name;
    }

    public String getAuthority()
    {
        return this.name;
    }

    public static UserRole of(String name) throws IllegalArgumentException
    {
        for (final UserRole value : values())
        {
            if (value.name.equals(name))
                return value;
        }
        throw new IllegalArgumentException("No UserRole value for name " + name);
    }
}

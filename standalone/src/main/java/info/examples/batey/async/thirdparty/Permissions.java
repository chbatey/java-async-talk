package info.examples.batey.async.thirdparty;

import com.google.common.collect.Sets;

import java.util.Objects;
import java.util.Set;

public class Permissions {

    public static Permissions permissions(String... permissions) {
        return new Permissions(Sets.newHashSet(permissions));
    }

    private Set<String> permissions;

    Permissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }

    @Override
    public String toString() {
        return "Permissions{" +
                "permissions=" + permissions +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permissions that = (Permissions) o;
        return Objects.equals(permissions, that.permissions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(permissions);
    }
}

package info.examples.batey.async.thirdparty;

import java.util.Objects;

public class User {
    private String name;
    private String userId;

    public User(String name, String userName) {
        this.name = name;
        this.userId = userName;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(name, user.name) &&
                Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, userId);
    }
}

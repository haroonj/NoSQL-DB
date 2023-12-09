package com.example.node.model.system;

import lombok.*;

import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User {
    private String _id;
    private String full_name;
    private String username;
    private String password;
    private String role;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(_id, user._id) && Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, username);
    }
}

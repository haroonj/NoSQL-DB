package com.example.bootstrapping.model.system;

import lombok.*;

import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Node {
    private String _id;
    private String name;
    private String url;
    private long numOfAffinities;
    private long numOfUsers;
    private int port;
    public void addAffinity() {
        numOfAffinities++;
    }

    public void removeAffinity() {
        numOfAffinities--;
    }

    public void addUsers() {
        numOfUsers++;
    }

    public void removeUsers() {
        numOfUsers--;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(_id, node._id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id);
    }
}

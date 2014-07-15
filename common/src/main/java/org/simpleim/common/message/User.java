package org.simpleim.common.message;

public class User {

    private String id;

    private String nikename;

    public String getId() {
        return id;
    }

    public User setId(String id) {
        this.id = id;
        return this;
    }

    public String getNikename() {
        return nikename;
    }

    public User setNikename(String nikename) {
        this.nikename = nikename;
        return this;
    }
}

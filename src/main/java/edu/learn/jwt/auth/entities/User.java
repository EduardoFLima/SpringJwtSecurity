package edu.learn.jwt.auth.entities;

import edu.learn.jwt.auth.utils.Role;

import javax.persistence.*;

@Entity
@Table(name = "user")
public class User {

    private String username;
    private String password;
    private Role role;

    public User() { }

    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }


    @Id
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name= "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Enumerated(EnumType.STRING)
    @Column(name= "role")
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!getUsername().equals(user.getUsername())) return false;
        if (!getRole().equals(user.getRole())) return false;
        return getPassword().equals(user.getPassword());
    }

    @Override
    public int hashCode() {
        int result = getUsername().hashCode();
        result = 31 * result + getRole().hashCode();
        result = 31 * result + getPassword().hashCode();
        return result;
    }
}

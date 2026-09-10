package com.customersummary.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FakeStoreCustomer {
    private int id;
    private String email;
    private FakeStoreName name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public FakeStoreName getName() {
        return name;
    }

    public void setName(FakeStoreName name) {
        this.name = name;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FakeStoreName {
        private String firstname;
        private String lastname;

        public String getFirstname() {
            return firstname;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }
    }
}

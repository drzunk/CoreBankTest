package com.corebank.models;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfo {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    private int id;
    private String name;
    private String email;

    // --- THÊM DÒNG NÀY ĐỂ HỨNG OBJECT CON ---
    private Address address;

    // Các Getter/Setter cũ của id, name, email giữ nguyên...
    // Nhớ dùng Alt+Insert để đẻ thêm Getter/Setter cho biến address này nhé!
}
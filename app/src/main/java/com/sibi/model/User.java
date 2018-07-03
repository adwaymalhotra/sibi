package com.sibi.model;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adway on 23/11/17.
 */

public class User {
    private String email;
    private String name;
    private double monthlyBudget;
    private String json = new Gson().toJson(new PhotoDTO());
    private List<Category> categories = new ArrayList<>();

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public double getMonthlyBudget() {
        return monthlyBudget;
    }

    public void setMonthlyBudget(double monthlyBudget) {
        this.monthlyBudget = monthlyBudget;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override public int hashCode() {
        int result = email.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return email.equals(user.email) && name.equals(user.name);
    }
}

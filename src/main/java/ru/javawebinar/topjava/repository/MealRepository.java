package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealRepository {

    List<Meal> getAll();

    Meal getById(long id);

    Meal save(Meal meal);

    boolean deleteById(long id);
}

package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, ConcurrentHashMap<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(meal -> this.save(meal, SecurityUtil.authUserId()));
    }

    @Override
    public Meal save(Meal meal, int userId) {
        if (meal.isNew()) {
            repository.computeIfAbsent(userId, id -> new ConcurrentHashMap<>());
            meal.setId(counter.incrementAndGet());
            repository.get(userId).put(meal.getId(), meal);
            return meal;
        }
        // handle case: update, but not present in storage
        ConcurrentHashMap<Integer, Meal> userMealRepo = repository.get(userId);
        if (userMealRepo != null) {
            return userMealRepo.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
        }
        return null;
    }

    @Override
    public boolean delete(int id, int userId) {
        ConcurrentHashMap<Integer, Meal> userMealRepo = repository.get(userId);
        if (userMealRepo != null) {
            return userMealRepo.remove(id) != null;
        }
        return false;
    }

    @Override
    public Meal get(int id, int userId) {
        ConcurrentHashMap<Integer, Meal> userMealRepo = repository.get(userId);
        if (userMealRepo != null) {
            return userMealRepo.get(id);
        }
        return null;
    }

    @Override
    public List<Meal> getAll(int userId) {
        ConcurrentHashMap<Integer, Meal> userMealRepo = repository.get(userId);
        if (userMealRepo != null) {
            List<Meal> meals = new ArrayList<>(userMealRepo.values());
            meals.sort(Comparator.comparing(Meal::getDateTime).reversed());
            return meals;
        }
        return null;
    }
}


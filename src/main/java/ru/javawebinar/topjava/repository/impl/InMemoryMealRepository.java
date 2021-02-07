package ru.javawebinar.topjava.repository.impl;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryMealRepository implements MealRepository {
    private final AtomicLong counter;
    private final Map<Long, Meal> memoryRepo;

    {
        counter = new AtomicLong(0);
        memoryRepo = new ConcurrentHashMap<>();
        MealsUtil.MEALS.forEach(this::save);
    }

    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(memoryRepo.values());
    }

    @Override
    public Meal getById(long id) {
        return memoryRepo.get(id);
    }

    @Override
    public synchronized Meal save(Meal meal) {
        if (meal.getId() == 0) {
            saveToMemoryRepo(meal);
        } else {
            Meal memMeal = memoryRepo.get(meal.getId());
            if (memMeal != null) {
                memoryRepo.put(meal.getId(), meal);
            } else {
                return null;
            }
        }
        return meal;
    }

    @Override
    public boolean deleteById(long id) {
        Meal meal = memoryRepo.remove(id);
        return meal != null;
    }

    private void saveToMemoryRepo(Meal meal) {
        meal.setId(counter.incrementAndGet());
        memoryRepo.put(counter.get(), meal);
    }
}

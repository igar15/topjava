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
        MealsUtil.meals.forEach(this::save);
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
    public Meal save(Meal meal) {
        if (meal.getId() == 0) {
            saveToMemoryRepo(meal);
            return meal;
        } else {
            return memoryRepo.computeIfPresent(meal.getId(), (key, oldValue) -> meal);
        }
    }

    @Override
    public boolean deleteById(long id) {
        return memoryRepo.remove(id) != null;
    }

    private void saveToMemoryRepo(Meal meal) {
        long id = counter.incrementAndGet();
        meal.setId(id);
        memoryRepo.put(id, meal);
    }
}

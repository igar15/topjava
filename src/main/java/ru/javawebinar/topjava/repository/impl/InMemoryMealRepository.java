package ru.javawebinar.topjava.repository.impl;

import ru.javawebinar.topjava.exceptions.MealNotFoundException;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryMealRepository implements MealRepository {
    private static final AtomicLong counter;
    private static final Map<Long, Meal> memoryRepo;

    static {
        counter = new AtomicLong(0);
        memoryRepo = new ConcurrentHashMap<>();
        MealsUtil.MEALS.forEach(InMemoryMealRepository::saveToMemoryRepo);
    }

    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(memoryRepo.values());
    }

    @Override
    public Meal getById(long id) {
        Meal meal = memoryRepo.get(id);
        checkForNull(meal, id);
        return meal;
    }

    @Override
    public Meal save(Meal meal) {
        if (meal.getId() == 0) {
            saveToMemoryRepo(meal);
        } else {
            Meal memMeal = memoryRepo.get(meal.getId());
            checkForNull(memMeal, meal.getId());
            memoryRepo.put(meal.getId(), meal);
        }
        return meal;
    }

    @Override
    public void deleteById(long id) {
        Meal meal = memoryRepo.remove(id);
        checkForNull(meal, id);
    }

    private static void saveToMemoryRepo(Meal meal) {
        meal.setId(counter.incrementAndGet());
        memoryRepo.put(counter.get(), meal);
    }

    private static void checkForNull(Meal meal, long id) {
        if (meal == null) {
            throw new MealNotFoundException("Not found meal with id=" + id);
        }
    }
}

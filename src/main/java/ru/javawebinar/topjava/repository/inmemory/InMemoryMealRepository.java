package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, ConcurrentHashMap<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.user1Meals.forEach(meal -> this.save(meal, 1));
        MealsUtil.user2Meals.forEach(meal -> this.save(meal, 2));
    }

    @Override
    public Meal save(Meal meal, int userId) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            repository.computeIfAbsent(userId, id -> new ConcurrentHashMap<>()).put(meal.getId(), meal);
            return meal;
        }
        // handle case: update, but not present in storage
        ConcurrentHashMap<Integer, Meal> userMealRepo = repository.get(userId);
        return userMealRepo == null ? null : userMealRepo.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int id, int userId) {
        ConcurrentHashMap<Integer, Meal> userMealRepo = repository.get(userId);
        return userMealRepo != null && userMealRepo.remove(id) != null;
    }

    @Override
    public Meal get(int id, int userId) {
        ConcurrentHashMap<Integer, Meal> userMealRepo = repository.get(userId);
        return userMealRepo == null ? null : userMealRepo.get(id);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return filterByPredicate(userId, meal -> true);
    }

    @Override
    public List<Meal> getAllFiltered(int userId, LocalDate startDate, LocalDate endDate) {
        return filterByPredicate(userId, meal -> DateTimeUtil.isBetweenIncluded(meal.getDate(), startDate, endDate));
    }

    private List<Meal> filterByPredicate(int userId, Predicate<Meal> filter) {
        ConcurrentHashMap<Integer, Meal> userMealRepo = repository.get(userId);
        return userMealRepo == null ? Collections.emptyList() : userMealRepo.values().stream()
                .filter(filter)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }
}


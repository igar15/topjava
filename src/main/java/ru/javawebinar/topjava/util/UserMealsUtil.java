package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class UserMealsUtil {

    public static void main(String[] args) {

        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println("filteredByStreams: " + filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));

        System.out.println("filteredByStreamInOnePass: " + filteredByStreamInOnePass(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesByDay = new HashMap<>();
        for (UserMeal userMeal : meals) {
            caloriesByDay.merge(userMeal.getDate(), userMeal.getCalories(), Integer::sum);
        }

        List<UserMealWithExcess> filteredUserMealsWithExcess = new ArrayList<>();
        for (UserMeal userMeal : meals) {
            if (TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime)) {
                filteredUserMealsWithExcess.add(createUserMealWithExcess(userMeal, caloriesByDay.get(userMeal.getDate()) > caloriesPerDay));
            }
        }

        return filteredUserMealsWithExcess;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesByDay = meals.stream()
                .collect(Collectors.groupingBy(UserMeal::getDate, Collectors.summingInt(UserMeal::getCalories)));

        return meals.stream()
                .filter(userMeal -> TimeUtil.isBetweenHalfOpen(userMeal.getTime(), startTime, endTime))
                .map(userMeal -> createUserMealWithExcess(userMeal, caloriesByDay.get(userMeal.getDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    public static List<UserMealWithExcess> filteredByStreamInOnePass(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        class CaloriesCounter {
            private int calories;
            private final List<UserMeal> filteredUserMeals = new ArrayList<>();

            public CaloriesCounter(UserMeal userMeal, LocalTime startTime, LocalTime endTime) {
                calories = userMeal.getCalories();
                if (TimeUtil.isBetweenHalfOpen(userMeal.getTime(), startTime, endTime)) {
                    filteredUserMeals.add(userMeal);
                }
            }
        }

        class UserMealWithExcessCollector implements Collector<UserMeal, Map<LocalDate, CaloriesCounter>, List<UserMealWithExcess>> {
            @Override
            public Supplier<Map<LocalDate, CaloriesCounter>> supplier() {
                return HashMap::new;
            }

            @Override
            public BiConsumer<Map<LocalDate, CaloriesCounter>, UserMeal> accumulator() {
                return (localDateCaloriesCounterMap, userMeal) -> localDateCaloriesCounterMap.merge(userMeal.getDate(),
                                                                            new CaloriesCounter(userMeal, startTime, endTime), (oldValue, newValue) -> {
                    oldValue.calories += userMeal.getCalories();
                    if (TimeUtil.isBetweenHalfOpen(userMeal.getTime(), startTime, endTime)) {
                        oldValue.filteredUserMeals.add(userMeal);
                    }
                    return oldValue;
                });
            }

            @Override
            public BinaryOperator<Map<LocalDate, CaloriesCounter>> combiner() {
                return (map1, map2) -> {
                    map2.forEach(((localDate, caloriesCounter) -> map1.merge(localDate, caloriesCounter, (oldValue, newValue) -> {
                        oldValue.calories += newValue.calories;
                        oldValue.filteredUserMeals.addAll(newValue.filteredUserMeals);
                        return oldValue;
                    })));
                    return map1;
                };
            }

            @Override
            public Function<Map<LocalDate, CaloriesCounter>, List<UserMealWithExcess>> finisher() {
                return localDateCaloriesCounterMap -> localDateCaloriesCounterMap.values().stream()
                        .flatMap(caloriesCounter -> caloriesCounter.filteredUserMeals.stream().map(userMeal -> createUserMealWithExcess(userMeal, caloriesCounter.calories > caloriesPerDay)))
                        .collect(Collectors.toList());
            }

            @Override
            public Set<Characteristics> characteristics() {
                return EnumSet.of(Characteristics.CONCURRENT);
            }
        }

        return meals.stream()
                .collect(new UserMealWithExcessCollector());
    }

    public static UserMealWithExcess createUserMealWithExcess(UserMeal userMeal, boolean excess) {
        return new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), excess);
    }
}

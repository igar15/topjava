package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

        System.out.println("filteredByCyclesInOnePass: " + filteredByCyclesInOnePass(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));

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

    public static List<UserMealWithExcess> filteredByCyclesInOnePass(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return null;
    }

    public static List<UserMealWithExcess> filteredByStreamInOnePass(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return meals.stream()
                .collect(Collectors.groupingBy(UserMeal::getDate))
                .values()
                .stream()
                .collect(Collectors.groupingBy(list -> list, Collectors.summingInt(list -> list.stream().flatMapToInt(userMeal -> IntStream.of(userMeal.getCalories())).sum())))
                .entrySet()
                .stream()
                .flatMap(listIntegerEntry -> listIntegerEntry.getKey().stream().map(userMeal -> createUserMealWithExcess(userMeal, listIntegerEntry.getValue() > caloriesPerDay)))
                .filter(userMealWithExcess -> TimeUtil.isBetweenHalfOpen(userMealWithExcess.getTime(), startTime, endTime))
                .collect(Collectors.toList());
    }

    public static UserMealWithExcess createUserMealWithExcess(UserMeal userMeal, boolean excess) {
        return new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), excess);
    }
}

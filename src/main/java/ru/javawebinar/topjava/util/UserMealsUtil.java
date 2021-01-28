package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
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

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));

        System.out.println(filteredByCyclesInOnePass(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));

        System.out.println(filteredByStreamInOnePass(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        Map<LocalDate, Integer> caloriesByDay = new HashMap<>();
        for (UserMeal userMeal : meals) {
            caloriesByDay.merge(userMeal.getDate(), userMeal.getCalories(), Integer::sum);
        }

        List<UserMealWithExcess> filteredUserMealsWithExcess = new ArrayList<>();
        for (UserMeal userMeal : meals) {
            if (TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime)) {
                filteredUserMealsWithExcess.add(new UserMealWithExcess(userMeal, caloriesByDay.get(userMeal.getDate()) > caloriesPerDay));
            }
        }

        return filteredUserMealsWithExcess;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        Map<LocalDate, Integer> caloriesByDay = meals.stream()
                .collect(Collectors.groupingBy(UserMeal::getDate, Collectors.summingInt(UserMeal::getCalories)));

        return meals.stream()
                .filter(userMeal -> TimeUtil.isBetweenHalfOpen(userMeal.getTime(), startTime, endTime))
                .map(userMeal -> new UserMealWithExcess(userMeal, caloriesByDay.get(userMeal.getDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    public static List<UserMealWithExcess> filteredByCyclesInOnePass(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        List<UserMeal> mealsCopy = new ArrayList<>(meals);

        Comparator<UserMeal> timeIntervalComparator = (userMeal1, userMeal2) -> {
            if (!userMeal1.getDate().equals(userMeal2.getDate())) {
                return userMeal1.getDateTime().compareTo(userMeal2.getDateTime());
            }
            boolean isUserMeal1InInterval = TimeUtil.isBetweenHalfOpen(userMeal1.getTime(), startTime, endTime);
            boolean isUserMeal2InInterval = TimeUtil.isBetweenHalfOpen(userMeal2.getTime(), startTime, endTime);

            if (!isUserMeal1InInterval && !isUserMeal2InInterval) {
                return userMeal1.getDateTime().compareTo(userMeal2.getDateTime());
            } else if (isUserMeal1InInterval && isUserMeal2InInterval) {
                return userMeal1.getDateTime().compareTo(userMeal2.getDateTime());
            } else if (isUserMeal1InInterval && !isUserMeal2InInterval) {
                return 1;
            } else if (!isUserMeal1InInterval && isUserMeal2InInterval) {
                return -1;
            }
            return 0;
        };

        mealsCopy.sort(timeIntervalComparator);

        List<UserMealWithExcess> filteredUserMealsWithExcess = new ArrayList<>();
        LocalDate tempLocalDate = mealsCopy.get(0).getDate();
        int tempCaloriesCounter = 0;
        for (int i = 0; i < mealsCopy.size(); i++) {
            UserMeal userMeal = mealsCopy.get(i);
            if (!userMeal.getDate().equals(tempLocalDate)) {
                int tempListIndex = i - 1;
                while (true) {
                    UserMeal tempUserMeal = mealsCopy.get(tempListIndex);
                    if (TimeUtil.isBetweenHalfOpen(tempUserMeal.getTime(), startTime, endTime)) {
                        filteredUserMealsWithExcess.add(new UserMealWithExcess(tempUserMeal, tempCaloriesCounter > caloriesPerDay));
                        tempListIndex--;
                    }
                    else {
                        tempLocalDate = userMeal.getDate();
                        tempCaloriesCounter = userMeal.getCalories();
                        break;
                    }
                }
            } else if (i == mealsCopy.size() - 1) {
                int tempListIndex = i;
                while (true) {
                    UserMeal tempUserMeal = mealsCopy.get(tempListIndex);
                    tempCaloriesCounter += tempUserMeal.getCalories();
                    if (TimeUtil.isBetweenHalfOpen(tempUserMeal.getTime(), startTime, endTime)) {
                        filteredUserMealsWithExcess.add(new UserMealWithExcess(tempUserMeal, tempCaloriesCounter > caloriesPerDay));
                        tempListIndex--;
                    }
                    else {
                        break;
                    }
                }
            } else {
                tempCaloriesCounter += userMeal.getCalories();
            }
        }

        return filteredUserMealsWithExcess;
    }

    public static List<UserMealWithExcess> filteredByStreamInOnePass(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        List<UserMealWithExcess> filteredUserMealsWithExcess = new ArrayList<>();

        meals.stream()
                .collect(Collectors.groupingBy(UserMeal::getDate))
                .values()
                .forEach(list -> {
                    int caloriesByDate = list.stream().mapToInt(UserMeal::getCalories).sum();
                    filteredUserMealsWithExcess.addAll(list.stream()
                            .filter(userMeal -> TimeUtil.isBetweenHalfOpen(userMeal.getTime(), startTime, endTime))
                            .map(userMeal -> new UserMealWithExcess(userMeal, caloriesByDate > caloriesPerDay))
                            .collect(Collectors.toList()));
                });

        return filteredUserMealsWithExcess;
    }


    }

package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {
    public static final int USER_MEAL_1_ID = START_SEQ + 2;
    public static final int USER_MEAL_2_ID = START_SEQ + 3;
    public static final int USER_MEAL_3_ID = START_SEQ + 4;
    public static final int USER_MEAL_4_ID = START_SEQ + 5;
    public static final int ADMIN_MEAL_1_ID = START_SEQ + 6;
    public static final int NOT_FOUND = 10;

    public static final Meal userMeal1 = new Meal(USER_MEAL_1_ID, LocalDateTime.of(2020, 1, 30, 10, 0), "USER breakfast", 500);
    public static final Meal userMeal2 = new Meal(USER_MEAL_2_ID, LocalDateTime.of(2020, 1, 30, 13, 0), "USER lunch", 1000);
    public static final Meal userMeal3 = new Meal(USER_MEAL_3_ID, LocalDateTime.of(2020, 1, 30, 20, 0), "USER dinner", 500);
    public static final Meal userMeal4 = new Meal(USER_MEAL_4_ID, LocalDateTime.of(2020, 2, 1, 20, 0), "USER dinner", 500);

    public static final LocalDate START_DATE = LocalDate.of(2020, 1, 30);
    public static final LocalDate END_DATE = LocalDate.of(2020, 1, 31);

    public static Meal getNew() {
        return new Meal(null, LocalDateTime.of(2021, 2, 20, 17, 0),"new meal", 777);
    }

    public static Meal getUpdated() {
        Meal updated = new Meal(userMeal1);
        updated.setDateTime(LocalDateTime.of(2021, 2, 20, 21, 0));
        updated.setDescription("updated meal");
        updated.setCalories(555);
        return updated;
    }

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Meal... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}

package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;
import static ru.javawebinar.topjava.util.DateTimeUtil.DATE_TIME_FORMATTER;

public class MealTestData {
    public static final int USER_MEAL_1_ID = START_SEQ + 2;
    public static final int USER_MEAL_2_ID = START_SEQ + 3;
    public static final int USER_MEAL_3_ID = START_SEQ + 4;
    public static final int USER_MEAL_4_ID = START_SEQ + 5;
    public static final int ADMIN_MEAL_1_ID = START_SEQ + 6;

    public static final Meal userMeal1 = new Meal(USER_MEAL_1_ID, LocalDateTime.parse("2020-01-30 10:00", DATE_TIME_FORMATTER), "USER breakfast", 500);
    public static final Meal userMeal2 = new Meal(USER_MEAL_2_ID, LocalDateTime.parse("2020-01-30 13:00", DATE_TIME_FORMATTER), "USER lunch", 1000);
    public static final Meal userMeal3 = new Meal(USER_MEAL_3_ID, LocalDateTime.parse("2020-01-30 20:00", DATE_TIME_FORMATTER), "USER dinner", 500);
    public static final Meal userMeal4 = new Meal(USER_MEAL_4_ID, LocalDateTime.parse("2020-02-01 20:00", DATE_TIME_FORMATTER), "USER dinner", 500);

    public static final LocalDate START_DATE = LocalDate.parse("2020-01-30");
    public static final LocalDate END_DATE = LocalDate.parse("2020-01-31");

    public static Meal getNew() {
        return new Meal(null, LocalDateTime.parse("2021-02-20 17:00", DATE_TIME_FORMATTER), "new meal", 777);
    }

    public static Meal getUpdated() {
        Meal updated = new Meal(userMeal1);
        updated.setDateTime(LocalDateTime.parse("2021-02-20 21:00", DATE_TIME_FORMATTER));
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

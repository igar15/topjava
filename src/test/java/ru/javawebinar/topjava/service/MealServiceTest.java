package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.util.List;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.NOT_FOUND;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ContextConfiguration({"classpath:spring/spring-app.xml", "classpath:spring/spring-db.xml"})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService mealService;

    @Test
    public void get() {
        Meal meal = mealService.get(USER_MEAL_1_ID, USER_ID);
        assertMatch(meal, userMeal1);
    }

    @Test
    public void getNotFound() {
        assertThrows(NotFoundException.class, () -> mealService.get(NOT_FOUND, USER_ID));
    }

    @Test
    public void getNotOwn() {
        assertThrows(NotFoundException.class, () -> mealService.get(ADMIN_MEAL_1_ID, USER_ID));
    }

    @Test
    public void delete() {
        mealService.delete(USER_MEAL_1_ID, USER_ID);
        assertThrows(NotFoundException.class, () -> mealService.get(USER_MEAL_1_ID, USER_ID));
    }

    @Test
    public void deleteNotFound() {
        assertThrows(NotFoundException.class, () -> mealService.delete(NOT_FOUND, USER_ID));
    }

    @Test
    public void deleteNotOwn() {
        assertThrows(NotFoundException.class, () -> mealService.delete(ADMIN_MEAL_1_ID, USER_ID));
    }

    @Test
    public void getBetweenInclusive() {
        List<Meal> meals = mealService.getBetweenInclusive(START_DATE, END_DATE, USER_ID);
        assertMatch(meals, userMeal3, userMeal2, userMeal1);
    }

    @Test
    public void getAll() {
        List<Meal> meals = mealService.getAll(USER_ID);
        assertMatch(meals, userMeal4, userMeal3, userMeal2, userMeal1);
    }

    @Test
    public void update() {
        mealService.update(getUpdated(), USER_ID);
        assertMatch(mealService.get(USER_MEAL_1_ID, USER_ID), getUpdated());
    }

    @Test
    public void updateNotOwn() {
        Meal meal = getUpdated();
        meal.setId(ADMIN_MEAL_1_ID);
        assertThrows(NotFoundException.class, () -> mealService.update(meal, USER_ID));
    }

    @Test
    public void create() {
        Meal created = mealService.create(getNew(), USER_ID);
        Integer newId = created.getId();
        Meal newMeal = getNew();
        newMeal.setId(newId);
        assertMatch(created, newMeal);
        assertMatch(mealService.get(newId, USER_ID), newMeal);
    }

    @Test
    public void duplicateDateTimeCreate() {
        Meal meal = new Meal(userMeal1);
        meal.setId(null);
        assertThrows(DataAccessException.class, () -> mealService.create(meal, USER_ID));
    }
}
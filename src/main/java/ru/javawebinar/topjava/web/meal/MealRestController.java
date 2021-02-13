package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.*;
import static ru.javawebinar.topjava.web.SecurityUtil.*;

@Controller
public class MealRestController {
    private static final Logger log = LoggerFactory.getLogger(MealRestController.class);

    @Autowired
    private MealService service;

    public Meal create(Meal meal) {
        log.info("create {} for user with id={}", meal, authUserId());
        checkNew(meal);
        return service.create(meal, authUserId());
    }

    public void update(Meal meal, int id) {
        log.info("update {} with id={} for user with id={}", meal, id, authUserId());
        assureIdConsistent(meal, id);
        service.update(meal, authUserId());
    }

    public void delete(int id) {
        log.info("delete {} for user with id={}", id, authUserId());
        service.delete(id, authUserId());
    }

    public Meal get(int id) {
        log.info("get {} for user with id={}", id, authUserId());
        return service.get(id, authUserId());
    }

    public List<MealTo> getAll() {
        log.info("getAll for user with id={}", authUserId());
        return (MealsUtil.getTos(service.getAll(authUserId()), authUserCaloriesPerDay()));
    }

}
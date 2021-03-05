package ru.javawebinar.topjava.service;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.service.abstracts.AbstractMealServiceTest;

@ActiveProfiles(profiles = Profiles.JDBC)
public class MealServiceJdbcTest extends AbstractMealServiceTest {
}
package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @Test
    public void get() {
        Meal meal = service.get(1, USER_ID);
        assertMatch(meal, MEAL_1);
    }

    @Test(expected = NotFoundException.class)
    public void getAnother() {
        service.get(1, ADMIN_ID);
    }

    @Test
    public void delete() {
        service.delete(MEAL_1.getId(), USER_ID);
        assertMatch(service.getAll(USER_ID), MEAL_6, MEAL_5, MEAL_4, MEAL_3, MEAL_2);
    }

    @Test(expected = NotFoundException.class)
    public void deleteAnother() {
        service.delete(MEAL_1.getId(), ADMIN_ID);
    }

    @Test
    public void getBetweenDates() {
        assertMatch(service.getBetweenDates(LocalDate.of(2015, 05, 01),
                LocalDate.of(2015, 05,  30), USER_ID), MEAL_3, MEAL_2, MEAL_1);
    }

    @Test
    public void getBetweenDateTimes() {
        assertMatch(service.getBetweenDateTimes(LocalDateTime.of(2015, 05, 01, 00, 00),
                LocalDateTime.of(2015, 05, 30, 15, 00), USER_ID),
                MEAL_2, MEAL_1);
    }

    @Test
    public void getAll() {
        assertMatch(service.getAll(USER_ID), MEAL_6, MEAL_5, MEAL_4, MEAL_3, MEAL_2, MEAL_1);
    }

    @Test
    public void update() {
        Meal updated = new Meal(MEAL_1);
        updated.setDescription("Updated");
        updated.setCalories(1000);
        service.update(updated, USER_ID);
        assertMatch(service.get(updated.getId(), USER_ID), updated);
    }

    @Test(expected = NotFoundException.class)
    public void updateAnother() {
        Meal updated = new Meal(MEAL_1);
        updated.setDescription("Updated");
        updated.setCalories(1000);
        service.update(updated, ADMIN_ID);
    }

    @Test
    public void create() {
        Meal newMeal = new Meal(NEW_MEAL);
        Meal created = service.create(newMeal, USER_ID);
        newMeal.setId(created.getId());
        assertMatch(service.getAll(USER_ID), MEAL_6, MEAL_5, MEAL_4, MEAL_3, MEAL_2, newMeal, MEAL_1);
    }
}
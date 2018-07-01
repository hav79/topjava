package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealWithExceed;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryMealDao implements MealDao {

    private static AtomicInteger counter = new AtomicInteger(0);

    private final CopyOnWriteArrayList<Meal> meals = new CopyOnWriteArrayList<>(Arrays.asList(
            new Meal(getNextId(), LocalDateTime.of(2015, Month.MAY, 30, 10, 0), "Завтрак", 500),
            new Meal(getNextId(), LocalDateTime.of(2015, Month.MAY, 30, 13, 0), "Обед", 1000),
            new Meal(getNextId(), LocalDateTime.of(2015, Month.MAY, 30, 20, 0), "Ужин", 500),
            new Meal(getNextId(), LocalDateTime.of(2015, Month.MAY, 31, 10, 0), "Завтрак", 1000),
            new Meal(getNextId(), LocalDateTime.of(2015, Month.MAY, 31, 13, 0), "Обед", 500),
            new Meal(getNextId(), LocalDateTime.of(2015, Month.MAY, 31, 20, 0), "Ужин", 510)
    ));

    @Override
    public void add(Meal meal) {
        if (meal.getId() == -1)
            meals.add(new Meal(getNextId(), meal));
        else
            meals.add(meal);
    }

    @Override
    public void delete(int id) {
        Meal meal = getById(id);
        meals.remove(meal);
    }

    @Override
    public void update(Meal meal) {
        Meal inDb = getById(meal.getId());
        if (inDb != null) {
            delete(inDb.getId());
            add(meal);
        }
        meals.sort(Comparator.comparingInt(Meal::getId));
    }

    @Override
    public List<MealWithExceed> getAll(int caloriesPerDay) {
        return MealsUtil.getWithExceeded(meals, caloriesPerDay);
    }

    @Override
    public List<MealWithExceed> getFiltered(LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return MealsUtil.getFilteredWithExceeded(meals, startTime, endTime, caloriesPerDay);
    }

    @Override
    public Meal getById(int id) {
        for (Meal meal : meals) {
            if (meal.getId() == id)
                return meal;
        }
        return null;
    }

    private int getNextId() {
        return counter.incrementAndGet();
    }
}

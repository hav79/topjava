package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealWithExceed;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryMealDao implements MealDao {

    private AtomicInteger counter = new AtomicInteger(0);

    private final ConcurrentHashMap<Integer, Meal> meals = new ConcurrentHashMap<>();

    public InMemoryMealDao() {
        int newId = getNextId();
        meals.put(newId, new Meal(newId, LocalDateTime.of(2015, Month.MAY, 30, 10, 0), "Завтрак", 500));
        newId = getNextId();
        meals.put(newId, new Meal(newId, LocalDateTime.of(2015, Month.MAY, 30, 13, 0), "Обед", 1000));
        newId = getNextId();
        meals.put(newId, new Meal(newId, LocalDateTime.of(2015, Month.MAY, 30, 20, 0), "Ужин", 500));
        newId = getNextId();
        meals.put(newId, new Meal(newId, LocalDateTime.of(2015, Month.MAY, 31, 10, 0), "Завтрак", 1000));
        newId = getNextId();
        meals.put(newId, new Meal(newId, LocalDateTime.of(2015, Month.MAY, 31, 13, 0), "Обед", 500));
        newId = getNextId();
        meals.put(newId, new Meal(newId, LocalDateTime.of(2015, Month.MAY, 31, 20, 0), "Ужин", 510));
    }

    @Override
    public void add(Meal meal) {
        if (meal.getId() == -1) {
            int newId = getNextId();
            meals.put(newId, new Meal(newId, meal));
        }
        else
            meals.put(meal.getId(), meal);
    }

    @Override
    public void delete(int id) {
        meals.remove(id);
    }

    @Override
    public void update(Meal meal) {
        meals.replace(meal.getId(), meal);
    }

    @Override
    public List<MealWithExceed> getAll(int caloriesPerDay) {
        return MealsUtil.getWithExceeded(new ArrayList<>(meals.values()), caloriesPerDay);
    }

    @Override
    public List<MealWithExceed> getFiltered(LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return MealsUtil.getFilteredWithExceeded(new ArrayList<>(meals.values()), startTime, endTime, caloriesPerDay);
    }

    @Override
    public Meal getById(int id) {
        return meals.get(id);
    }

    private int getNextId() {
        return counter.incrementAndGet();
    }
}

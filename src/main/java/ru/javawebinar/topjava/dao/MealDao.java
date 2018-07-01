package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealWithExceed;

import java.time.LocalTime;
import java.util.List;

public interface MealDao {

    void add(Meal meal);
    void delete(int id);
    void update(Meal meal);
    List<MealWithExceed> getAll(int caloriesPerDay);
    List<MealWithExceed> getFiltered(LocalTime startTime, LocalTime endTime, int caloriesPerDay);
    Meal getById(int id);
}

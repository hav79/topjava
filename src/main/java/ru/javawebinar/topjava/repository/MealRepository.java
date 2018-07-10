package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDate;
import java.util.List;

public interface MealRepository {
    Meal save(Meal meal, int authUser);

    boolean delete(int id, int authUser);

    Meal get(int id, int authUser);

    List<Meal> getAll(int authUser);

    List<Meal> getFiltered(int authUser, LocalDate start, LocalDate end);
}

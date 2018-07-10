package ru.javawebinar.topjava.service;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.util.List;


public interface MealService {

    Meal create(Meal meal, int authUser);

    void delete(int id, int authUser) throws NotFoundException;

    Meal get(int id, int authUser) throws NotFoundException;

    void update(Meal meal, int authUser);

    List<Meal> getAll(int authUser);

    List<Meal> getFiltered(int authUser, LocalDate start, LocalDate end);
}
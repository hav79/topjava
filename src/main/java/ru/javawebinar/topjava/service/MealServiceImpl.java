package ru.javawebinar.topjava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFoundWithId;


@Service
public class MealServiceImpl implements MealService {

    private MealRepository repository;

    @Autowired
    public MealServiceImpl(MealRepository repository) {
        this.repository = repository;
    }

    @Override
    public Meal create(Meal meal, int authUser) {
        return repository.save(meal, authUser);
    }

    @Override
    public void delete(int id, int authUser) throws NotFoundException {
        checkNotFoundWithId(repository.delete(id, authUser), id);
    }

    @Override
    public Meal get(int id, int authUser) throws NotFoundException {
        return checkNotFoundWithId(repository.get(id, authUser), id);
    }

    @Override
    public void update(Meal meal, int authUser) {
        checkNotFoundWithId(repository.save(meal, authUser), meal.getId());
    }

    @Override
    public List<Meal> getAll(int authUser) {
        return repository.getAll(authUser);
    }

    @Override
    public List<Meal> getFiltered(int authUser, LocalDate start, LocalDate end) {
        return repository.getFiltered(authUser, start, end);
    }
}
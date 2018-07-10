package ru.javawebinar.topjava.repository.mock;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Repository
public class InMemoryMealRepositoryImpl implements MealRepository {
    private Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.MEALS.forEach(m -> save(m, 1));
    }

    @Override
    public Meal save(Meal meal, int authUser) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            repository.put(meal.getId(), meal);
            return meal;
        }
        // treat case: update, but absent in storage
        return repository.computeIfPresent(meal.getId(), (id, oldMeal) -> oldMeal.getUserId() == authUser ?
                meal : oldMeal);
    }

    @Override
    public boolean delete(int id, int authUser) {
        if (get(id, authUser) == null)
            return false;
        return repository.remove(id) != null;
    }

    @Override
    public Meal get(int id, int authUser) {
        Meal meal = repository.get(id);
        if (meal == null || meal.getUserId() != authUser)
            return null;
        return meal;
    }

    @Override
    public List<Meal> getAll(int authUser) {
        return getFiltered(authUser, LocalDate.MIN, LocalDate.MAX);
    }

    @Override
    public List<Meal> getFiltered(int authUser, LocalDate start, LocalDate end) {
        return repository.values().stream()
                .filter(m -> m.getUserId() == authUser)
                .filter(m -> DateTimeUtil.isBetween(m.getDate(), start, end))
                .sorted(Comparator.comparing(Meal::getDate).reversed())
                .collect(Collectors.toList());
    }
}


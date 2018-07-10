package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealWithExceed;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class MealRestController {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private MealService service;

    public List<MealWithExceed> getAll() {
        log.info("getAll");
        return getFiltered(LocalDateTime.MIN, LocalDateTime.MAX);
    }

    public List<MealWithExceed> getFiltered(LocalDateTime start, LocalDateTime end) {
        log.info("getFiltered");
        return MealsUtil.getFilteredWithExceeded(service.getFiltered(SecurityUtil.authUserId(), start.toLocalDate(), end.toLocalDate()),
                                        MealsUtil.DEFAULT_CALORIES_PER_DAY, start.toLocalTime(), end.toLocalTime());
    }

    public void save(Meal meal) {
        log.info("save {}", meal);
        if (meal.isNew())
            service.create(meal, SecurityUtil.authUserId());
        else
            service.update(meal, SecurityUtil.authUserId());
    }

    public Meal get(int id) {
        log.info("get {}", id);
        return service.get(id, SecurityUtil.authUserId());
    }

    public void delete(int id) {
        log.info("delete {}", id);
        service.delete(id, SecurityUtil.authUserId());
    }
}
package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.dao.InMemoryMealDao;
import ru.javawebinar.topjava.dao.MealDao;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealWithExceed;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {

    private MealDao mealDao = new InMemoryMealDao();

    private static final Logger log = getLogger(MealServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String forward;
        String action = req.getParameter("action");

        if (action.equalsIgnoreCase("delete")) {
            int mealId = Integer.parseInt(req.getParameter("id"));
            log.debug("delete meal with id " + mealId);
            mealDao.delete(mealId);
            forward = "mealsList.jsp";
            req.setAttribute("meals", mealDao.getAll(2000));
        } else if (action.equalsIgnoreCase("edit")) {
            int mealId = Integer.parseInt(req.getParameter("id"));
            log.debug("update meal with id " + mealId);
            Meal meal = mealDao.getById(mealId);
            forward = "meal.jsp";
            req.setAttribute("meal", meal);
        } else if (action.equalsIgnoreCase("add")) {
            log.debug("insert new meal");
            forward = "meal.jsp";
        } else {
            log.debug("redirect to mealsList.jsp");
            List<MealWithExceed> mealsWithExceeded = mealDao.getAll(2000);
            forward = "mealsList.jsp";
            req.setAttribute("meals", mealsWithExceeded);
        }

        req.getRequestDispatcher(forward).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String id = req.getParameter("mealId");
        String description = req.getParameter("description");
        LocalDateTime dateTime = LocalDateTime.parse(req.getParameter("dateTime"));
        int calories = Integer.parseInt(req.getParameter("calories"));
        if (id == null || id.isEmpty()) {
            mealDao.add(new Meal(dateTime, description, calories));
        } else {
            mealDao.update(new Meal(Integer.parseInt(id), dateTime, description, calories));
        }

        List<MealWithExceed> mealsWithExceeded = mealDao.getAll(2000);
        req.setAttribute("meals", mealsWithExceeded);
        req.getRequestDispatcher("mealsList.jsp").forward(req, resp);
    }
}

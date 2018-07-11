package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.web.meal.MealRestController;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class MealServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(MealServlet.class);

    ConfigurableApplicationContext context;

    private MealRestController controller;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        context = new ClassPathXmlApplicationContext("spring/spring-app.xml");
        controller = context.getBean(MealRestController.class);
    }

    @Override
    public void destroy() {
        context.close();
        super.destroy();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");

        Meal meal = new Meal(id.isEmpty() ? null : Integer.valueOf(id),
                SecurityUtil.authUserId(),
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));

        log.info(meal.isNew() ? "Create {}" : "Update {}", meal);
        controller.save(meal);
        response.sendRedirect("meals");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        switch (action == null ? "all" : action) {
            case "delete":
                int id = getId(request);
                log.info("Delete {}", id);
                controller.delete(id);
                response.sendRedirect("meals");
                break;
            case "create":
            case "update":
                final Meal meal = "create".equals(action) ?
                        new Meal(SecurityUtil.authUserId(),
                                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES),
                                "", 1000) :
                        controller.get(getId(request));
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/mealForm.jsp").forward(request, response);
                break;
            case "all":
            default:
                log.info("getAll");
                request.setAttribute("meals",
                        controller.getFiltered(getStartDateTime(request), getEndDateTime(request)));
                request.setAttribute("startDate", request.getParameter("startDate"));
                request.setAttribute("endDate", request.getParameter("endDate"));
                request.setAttribute("startTime", request.getParameter("startTime"));
                request.setAttribute("endTime", request.getParameter("endTime"));
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
        }
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }

    private LocalDateTime getStartDateTime(HttpServletRequest request) {
        LocalDate date = hasParameter(request, "startDate")
                ? LocalDate.parse(request.getParameter("startDate"))
                : LocalDate.MIN;
        LocalTime time = hasParameter(request, "startTime")
                ? LocalTime.parse(request.getParameter("startTime"))
                : LocalTime.MIN;
        return LocalDateTime.of(date, time);
    }

    private LocalDateTime getEndDateTime(HttpServletRequest request) {
        LocalDate date = hasParameter(request, "endDate")
                ? LocalDate.parse(request.getParameter("endDate"))
                : LocalDate.MAX;
        LocalTime time = hasParameter(request, "endTime")
                ? LocalTime.parse(request.getParameter("endTime"))
                : LocalTime.MAX;
        return LocalDateTime.of(date, time);
    }

    private boolean hasParameter(HttpServletRequest request, String param) {
        return request.getParameter(param) != null && !request.getParameter(param).toString().isEmpty();
    }
}

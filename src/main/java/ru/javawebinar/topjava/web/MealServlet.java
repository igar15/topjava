package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.web.meal.MealRestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

public class MealServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(MealServlet.class);

    private MealRestController controller;
    private ConfigurableApplicationContext appContext;

    @Override
    public void init() {
        appContext = new ClassPathXmlApplicationContext("spring/spring-app.xml");
        controller = appContext.getBean(MealRestController.class);
    }

    @Override
    public void destroy() {
        appContext.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");

        Meal meal = new Meal(id.isEmpty() ? null : Integer.valueOf(id),
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));

        log.info(meal.isNew() ? "Create {}" : "Update {}", meal);
        if (meal.isNew()) {
            controller.create(meal);
        } else {
            controller.update(meal, Integer.parseInt(id));
        }
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
                        new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000) :
                        controller.get(getId(request));
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/mealForm.jsp").forward(request, response);
                break;
            case "all":
            default:
                log.info("getAll");
                String startDateParam = request.getParameter("startDate");
                String endDateParam = request.getParameter("endDate");
                String startTimeParam = request.getParameter("startTime");
                String endTimeParam = request.getParameter("endTime");
                List<MealTo> mealTos = null;
                if (isFilterNeeded(startDateParam, endDateParam, startTimeParam, endTimeParam)) {
                    LocalDate startDate = (startDateParam == null || startDateParam.isEmpty()) ? null : LocalDate.parse(startDateParam);
                    LocalDate endDate = (endDateParam == null || endDateParam.isEmpty()) ? null : LocalDate.parse(endDateParam);
                    LocalTime startTime = (startTimeParam == null || startTimeParam.isEmpty()) ? null : LocalTime.parse(startTimeParam);
                    LocalTime endTime = (endTimeParam == null || endTimeParam.isEmpty()) ? null : LocalTime.parse(endTimeParam);
                    mealTos = controller.getAllFiltered(startDate, endDate, startTime, endTime);
                } else {
                    mealTos = controller.getAll();
                }
                request.setAttribute("meals", mealTos);
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
        }
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }

    private boolean isFilterNeeded(String... filterParams) {
        boolean isFilterNeeded = false;
        for (String param : filterParams) {
            if (param != null) {
                isFilterNeeded = true;
            }
        }
        return isFilterNeeded;
    }
}

package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.repository.impl.InMemoryMealRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.MealsUtil.*;

public class MealServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(MealServlet.class);
    private final MealRepository mealRepository = new InMemoryMealRepository();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "all";
        } else {
            action = action.toLowerCase();
        }

        switch (action) {
            case "delete":
                long id = Long.parseLong(request.getParameter("id"));
                log.debug("delete meal with id={}", id);
                mealRepository.deleteById(id);
                response.sendRedirect("meals");
                break;
            case "update":
                id = Long.parseLong(request.getParameter("id"));
                Meal meal = mealRepository.getById(id);
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("mealForm.jsp").forward(request, response);
                break;
            case "create":
                request.getRequestDispatcher("mealForm.jsp").forward(request, response);
                break;
            default:
                List<MealTo> mealTos = filteredByStreams(mealRepository.getAll(), LocalTime.MIN, LocalTime.MAX, CALORIES_PER_DAY);
                request.setAttribute("mealTos", mealTos);
                log.debug("forward to meals");
                request.getRequestDispatcher("meals.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String description = request.getParameter("description");
        int calories = Integer.parseInt(request.getParameter("calories"));
        LocalDateTime dateTime = LocalDateTime.parse(request.getParameter("dateTime"));
        long id = 0;
        String mealId = request.getParameter("id");
        if (mealId != null && !mealId.isEmpty()) {
            id = Long.parseLong(mealId);
        }
        Meal meal = new Meal(id, dateTime, description, calories);
        if (id == 0) {
            log.debug("create {}", meal);
        } else {
            log.debug("update {}", meal);
        }
        mealRepository.save(meal);
        response.sendRedirect("meals");
    }
}

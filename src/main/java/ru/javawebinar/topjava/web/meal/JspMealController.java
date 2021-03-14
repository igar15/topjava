package ru.javawebinar.topjava.web.meal;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Controller
@RequestMapping("/meals")
public class JspMealController extends AbstractMealController {

    @PostMapping
    public String save(@RequestParam(required = false) Integer id,
                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime,
                       @RequestParam String description,
                       @RequestParam int calories) {
        Meal meal = new Meal(dateTime, description, calories);
        if (id == null) {
            super.create(meal);
        } else {
            super.update(meal, id);
        }
        return "redirect:/meals";
    }

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("meals", super.getAll());
        return "meals";
    }

    @GetMapping("/delete")
    public String deleteMeal(@RequestParam int id) {
        super.delete(id);
        return "redirect:/meals";
    }

    @GetMapping("/update")
    public String update(@RequestParam int id, Model model) {
        model.addAttribute("meal", super.get(id));
        return "mealForm";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("meal", new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000));
        return "mealForm";
    }

    @GetMapping("/filter")
    public String filter(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
                         Model model) {
        model.addAttribute("meals", super.getBetween(startDate, startTime, endDate, endTime));
        return "meals";
    }
}
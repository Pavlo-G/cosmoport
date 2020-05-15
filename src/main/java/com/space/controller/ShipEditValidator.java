package com.space.controller;

import com.space.model.Ship;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


import java.time.LocalDate;
import java.time.ZoneId;



@Component
public class ShipEditValidator implements Validator {


    @Override
    public boolean supports(Class<?> clazz) {
        return Ship.class.equals((clazz));
    }

    @Override
    public void validate(Object target, Errors errors) {

        Ship ship = (Ship) target;

        if (ship.getName() != null) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors,"name","400");
            if ( ship.getName().length() > 50) {
                errors.rejectValue("name", "empty Name");
            }
        }
        if (ship.getPlanet() != null) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors,"planet","400");
            if ( ship.getPlanet().length() > 50) {
                errors.rejectValue("planet", "empty Planet");
            }
        }
        if (ship.getProdDate() != null) {
            LocalDate localDate = ship.getProdDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            int year = localDate.getYear();
            if (year <= 2800 || year >= 3019) {
                errors.rejectValue("prodDate", "prodDate out of bounds");
            }
        }

        if (ship.getSpeed() != null) {

            if (ship.getSpeed() <= 0.01 || ship.getSpeed() >= 0.99) {
                errors.rejectValue("speed", "speed out of bounds");
            }
        }

        if (ship.getCrewSize() != null) {

            if (ship.getCrewSize() <= 1 || ship.getCrewSize() >= 9999) {
                errors.rejectValue("crewSize", "crewSize out of bounds");
            }
        }

    }
}

package com.space.controller;


import com.space.model.*;
import com.space.repository.ShipRepository;
import com.space.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@RestController

public class ShipController {

    @Autowired
    private ShipRepository shipRepository;

    @Autowired
    private ShipEditValidator shipEditValidator;
    @Autowired
    private NewShipValidator newShipValidator;


    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(shipEditValidator);
        binder.addValidators(newShipValidator);
    }


    // Paging and sorting within DB


    @GetMapping("/rest/ships")
    public ResponseEntity<List<Ship>> sortShips
            (@RequestParam(value = "order", required = false, defaultValue = "ID") ShipOrder order,
             @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
             @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize,
             @RequestParam(value = "name", required = false) String name,
             @RequestParam(value = "planet", required = false) String planet,
             @RequestParam(value = "shipType", required = false) ShipType shipType,
             @RequestParam(value = "after", required = false) Long after,
             @RequestParam(value = "before", required = false) Long before,
             @RequestParam(value = "isUsed", required = false) Boolean isUsed,
             @RequestParam(value = "minSpeed", required = false) Double minSpeed,
             @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
             @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
             @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
             @RequestParam(value = "minRating", required = false) Double minRating,
             @RequestParam(value = "maxRating", required = false) Double maxRating
            ) {

        Pageable sorted = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));
        Page<Ship> result = shipRepository.findAll(getSpecs(name, planet, shipType, after, before,
                isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating), sorted);
        return ResponseEntity.status(HttpStatus.OK).body(result.getContent());
    }


    //Create new ship and add to DB


    @PostMapping(path = "rest/ships/")
    public ResponseEntity<?> addNewShip(@RequestBody Ship shipProp, BindingResult resultCreate) {
        if (shipProp.getUsed() == null) {
            shipProp.setUsed(false);
        }
        //validating fields
        newShipValidator.validate(shipProp, resultCreate);

        if (resultCreate.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } else {
            Ship ship = new Ship();
            moveProperties(shipProp, ship);

            shipRepository.save(ship);
            return ResponseEntity.status(HttpStatus.OK).body(ship);
        }

    }


    //Update ship in DB


    @PostMapping("/rest/ships/{id}")
    public ResponseEntity<Ship> updateShip(@PathVariable(value = "id") Long shipId,
                                           @RequestBody Ship shipProp, BindingResult result) {
        //check if Body is empty
        //      if true return existing object referencing to given id
        // if false perform changing of the fields wich exists in the body
        if (shipProp.getId() == null
                && shipProp.getName() == null
                && shipProp.getPlanet() == null
                && shipProp.getShipType() == null
                && shipProp.getUsed() == null
                && shipProp.getProdDate() == null
                && shipProp.getCrewSize() == null
                && shipProp.getSpeed() == null
                && shipProp.getRating() == null) {

            return ResponseEntity.status(HttpStatus.OK).body(getShipById(shipId).getBody());
        } else {
            if (shipId == 0L) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            // validating body fields
            shipEditValidator.validate(shipProp, result);

            if (result.hasErrors()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            } else {
                Optional<Ship> ship = shipRepository.findById(shipId);
                if (ship.isPresent()) {
                    if (shipProp.getName() != null) {
                        ship.get().setName(shipProp.getName());
                    }
                    if (shipProp.getPlanet() != null) {
                        ship.get().setPlanet(shipProp.getPlanet());
                    }
                    if (shipProp.getProdDate() != null) {
                        ship.get().setProdDate(shipProp.getProdDate());
                    }
                    if (shipProp.getShipType() != null) {
                        ship.get().setShipType(shipProp.getShipType());
                    }
                    if (shipProp.getUsed() != null) {
                        ship.get().setUsed(shipProp.getUsed());
                    }
                    if (shipProp.getSpeed() != null) {
                        ship.get().setSpeed(shipProp.getSpeed());
                    }
                    if (shipProp.getCrewSize() != null) {
                        ship.get().setCrewSize(shipProp.getCrewSize());
                    }

                    Double newRating = calculatesRating(ship.get().getUsed(), ship.get().getSpeed(), ship.get().getProdDate());
                    ship.get().setRating(newRating);

                    shipRepository.save(ship.get());
                    return ResponseEntity.status(HttpStatus.OK).body(ship.get());

                } else return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

            }
        }
    }


    // Delete ship from DB


    @DeleteMapping("/rest/ships/{id}")
    public ResponseEntity<?> deleteShip(@PathVariable(value = "id") Long shipId) {
        if (shipId == 0L) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        Optional<Ship> ship = shipRepository.findById(shipId);
        if (ship.isPresent()) {
            shipRepository.delete(ship.get());
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }


    //Find Ship by ID


    @GetMapping("/rest/ships/{id}")
    public ResponseEntity<Ship> getShipById(@PathVariable(value = "id") Long shipId) {
        if (shipId == 0L) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        Optional<Ship> ship = shipRepository.findById(shipId);
        return ship.map(value -> ResponseEntity.status(HttpStatus.OK).body(value)).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    //Get Count of all ships in the base

    @GetMapping("/rest/ships/count")
    public int shipsCount(@RequestParam(value = "order", defaultValue = "ID") ShipOrder order,
                          @RequestParam(value = "pageNumber", defaultValue = "0") Integer pageNumber,
                          @RequestParam(value = "pageSize", defaultValue = "3") Integer pageSize,
                          @RequestParam(value = "name", required = false) String name,
                          @RequestParam(value = "planet", required = false) String planet,
                          @RequestParam(value = "shipType", required = false) ShipType shipType,
                          @RequestParam(value = "after", required = false) Long after,
                          @RequestParam(value = "before", required = false) Long before,
                          @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                          @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                          @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                          @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                          @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                          @RequestParam(value = "minRating", required = false) Double minRating,
                          @RequestParam(value = "maxRating", required = false) Double maxRating) {


        List<Ship> result = shipRepository.findAll(getSpecs(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating));

        return result.size();
    }


    //Calculate Ship Rating

    private Double calculatesRating(boolean isUsed, Double speed, Date prodDate) {

        LocalDate localDate = prodDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        int year = localDate.getYear();
        int currentYear = 3019;
        double coef;
        if (isUsed) {
            coef = 0.5;
        } else {
            coef = 1;
        }
        Double r = (80 * speed * coef) / (currentYear - year + 1);
        return new BigDecimal(String.valueOf(r)).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();


    }


    // Transfer fields from request body object to ship object

    private Ship moveProperties(Ship shipProp, Ship ship) {
        ship.setName(shipProp.getName());
        ship.setPlanet(shipProp.getPlanet());
        ship.setProdDate(shipProp.getProdDate());
        ship.setShipType(shipProp.getShipType());
        ship.setUsed(shipProp.getUsed());
        ship.setSpeed(shipProp.getSpeed());
        ship.setCrewSize(shipProp.getCrewSize());
        Double newRating = calculatesRating(ship.getUsed(), ship.getSpeed(), ship.getProdDate());
        ship.setRating(newRating);
        return ship;
    }


    // Get Specification that combine all specs for given fields
    //If field is null it's specification will return true and will not perform filtering for this field

    private Specification<Ship> getSpecs(String name,
                                         String planet,
                                         ShipType shipType,
                                         Long after,
                                         Long before,
                                         Boolean isUsed,
                                         Double minSpeed,
                                         Double maxSpeed,
                                         Integer minCrewSize,
                                         Integer maxCrewSize,
                                         Double minRating,
                                         Double maxRating) {
        Specification<Ship> sName = new NameSpecs(name);
        Specification<Ship> sPlanet = new PlanetSpecs(planet);
        Specification<Ship> sType = new ShipTypeSpecs(shipType);
        Specification<Ship> sDate = new DateSpecs(after, before);
        Specification<Ship> sUsed = new IsUsedSpecs(isUsed);
        Specification<Ship> sSpeed = new SpeedSpecs(minSpeed, maxSpeed);
        Specification<Ship> sCrew = new CrewSizeSpecs(minCrewSize, maxCrewSize);
        Specification<Ship> sRating = new RatingSpecs(minRating, maxRating);
        Specification<Ship> spec = Specification.where(sName)
                .and(sPlanet)
                .and(sType)
                .and(sDate)
                .and(sUsed)
                .and(sSpeed)
                .and(sCrew)
                .and(sRating);
        return spec;
    }

}
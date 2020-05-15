package com.space.service;

import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class ShipTypeSpecs implements Specification<Ship> {

    private ShipType shipType;

    public ShipTypeSpecs(ShipType shipType) {
        this.shipType = shipType;
    }


    @Override
    public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if (shipType == null) {
            return criteriaBuilder.isTrue(criteriaBuilder.literal(true)); // always true = no filtering
        }
        return criteriaBuilder.equal(root.get("shipType"), this.shipType);
    }
}

package com.space.service;

import com.space.model.Ship;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class SpeedSpecs implements Specification<Ship> {

    private Double minSpeed;
    private Double maxSpeed;

    public SpeedSpecs(Double minSpeed, Double maxSpeed) {

        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
    }


    @Override
    public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if (minSpeed == null && maxSpeed== null) {
            return criteriaBuilder.isTrue(criteriaBuilder.literal(true)); // always true = no filtering
        } else if (minSpeed != null&& maxSpeed== null){
            return criteriaBuilder.greaterThan(root.get("speed"), this.minSpeed);

        } else if (minSpeed == null) {
            return criteriaBuilder.lessThan(root.get("speed"), this.maxSpeed);
        }else
            return criteriaBuilder.between(root.get("speed"), this.minSpeed, this.maxSpeed);
    }

}

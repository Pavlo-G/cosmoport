package com.space.service;

import com.space.model.Ship;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class RatingSpecs implements Specification<Ship> {

    private Double minRating;
    private Double maxRating;

    public RatingSpecs(Double minRating, Double maxRating) {
        this.minRating = minRating;
        this.maxRating = maxRating;
    }


    @Override
    public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if (minRating == null &&maxRating== null) {
            return criteriaBuilder.isTrue(criteriaBuilder.literal(true)); // always true = no filtering
        } else if (minRating != null&& maxRating== null){
            return criteriaBuilder.greaterThan(root.get("rating"), this.minRating);

        } else if (minRating == null) {
            return criteriaBuilder.lessThan(root.get("rating"), this.maxRating);
        }else
            return criteriaBuilder.between(root.get("rating"), this.minRating, this.maxRating);
    }

}

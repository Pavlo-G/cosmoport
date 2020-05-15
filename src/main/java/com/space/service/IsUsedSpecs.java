package com.space.service;

import com.space.model.Ship;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class IsUsedSpecs implements Specification<Ship> {


   private Boolean isUsed;

    public IsUsedSpecs(Boolean isUsed) {
        this.isUsed = isUsed;
    }

    @Override
    public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if (isUsed == null) {
            return criteriaBuilder.isTrue(criteriaBuilder.literal(true)); // always true = no filtering
        }
        return criteriaBuilder.equal(root.get("isUsed"), this.isUsed);
    }
}

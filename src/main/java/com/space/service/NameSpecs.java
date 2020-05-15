package com.space.service;


import com.space.model.Ship;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class NameSpecs implements Specification<Ship> {

        private String name;

    public NameSpecs(String name) {
        this.name = name;
    }
// constructor omitted for brevity

    @Override
    public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if (name == null) {
            return criteriaBuilder.isTrue(criteriaBuilder.literal(true)); // always true = no filtering
        }
        return criteriaBuilder.like(root.get("name"), "%"+this.name+"%");
    }



}

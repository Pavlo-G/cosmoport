package com.space.service;

import com.space.model.Ship;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class CrewSizeSpecs implements Specification<Ship> {

private Integer minCrewSize;
private Integer maxCrewSize;

    public CrewSizeSpecs(Integer minCrewSize, Integer maxCrewSize) {
        this.minCrewSize = minCrewSize;
        this.maxCrewSize = maxCrewSize;
    }

    @Override
    public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if (minCrewSize== null && maxCrewSize== null) {
            return criteriaBuilder.isTrue(criteriaBuilder.literal(true)); // always true = no filtering
        } else if (minCrewSize != null&& maxCrewSize== null){
            return criteriaBuilder.greaterThan(root.get("crewSize"), this.minCrewSize);

        } else if (minCrewSize== null) {
            return criteriaBuilder.lessThan(root.get("crewSize"), this.maxCrewSize);
        }else
            return criteriaBuilder.between(root.get("crewSize"), this.minCrewSize, this.maxCrewSize);
    }
}

package com.space.service;

import com.space.model.Ship;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;

public class DateSpecs implements Specification<Ship> {


    private Date dAfter;
    private Date dBefore;

    public DateSpecs(Long after, Long before) {
        if (after != null) {
            dAfter = new Date(after);
        }
        if (before != null) {
            dBefore = new Date(before);
        }
    }


    @Override
    public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if (dAfter == null && dBefore == null) {
            return criteriaBuilder.isTrue(criteriaBuilder.literal(true)); // always true = no filtering
        } else if (dAfter != null&& dBefore == null){
                return criteriaBuilder.greaterThan(root.get("prodDate"), this.dAfter);

        } else if (dAfter == null) {
            return criteriaBuilder.lessThan(root.get("prodDate"), this.dBefore);
        }else
        return criteriaBuilder.between(root.get("prodDate"), this.dAfter, this.dBefore);
    }
}


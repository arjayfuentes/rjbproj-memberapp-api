package com.rjproj.memberapp.repository;
import com.rjproj.memberapp.model.Membership;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Repository
public class MembershipRepositoryCustomImpl implements MembershipRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Membership> findMembershipsByFilters(UUID organizationId, Map<String, Object> filters, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Membership> query = cb.createQuery(Membership.class);
        Root<Membership> membership = query.from(Membership.class);

        List<Predicate> predicates = new ArrayList<>();

        // Filtering by organizationId
        predicates.add(cb.equal(membership.get("organizationId"), organizationId));

        filters.forEach((key, value) -> {
            if (value != null) {
                switch (key) {
                    case "member.firstName":
                        Join<Object, Object> member = membership.join("member");
                        predicates.add(cb.like(cb.lower(member.get("firstName")), "%" + value.toString().toLowerCase() + "%"));
                        break;
                    case "member.email":
                        Join<Object, Object> memberEmail = membership.join("member");
                        predicates.add(cb.like(cb.lower(memberEmail.get("email")), "%" + value.toString().toLowerCase() + "%"));
                        break;
                    case "membershipType.name":
                        Join<Object, Object> membershipType = membership.join("membershipType");
                        predicates.add(cb.like(cb.lower(membershipType.get("name")), "%" + value.toString().toLowerCase() + "%"));
                        break;
                    case "membershipStatus.name":
                        Join<Object, Object> membershipStatus = membership.join("membershipStatus");
                        predicates.add(cb.like(cb.lower(membershipStatus.get("name")), "%" + value.toString().toLowerCase() + "%"));
                        break;
                    case "member.memberAddress.city":
                        Join<Object, Object> memberAddress = membership.join("member").join("memberAddress");
                        predicates.add(cb.like(cb.lower(memberAddress.get("city")), "%" + value.toString().toLowerCase() + "%"));
                        break;
                    case "startDate":
                        predicates.add(cb.greaterThanOrEqualTo(membership.get("startDate"), value.toString()));
                        break;
                    case "endDate":
                        predicates.add(cb.lessThanOrEqualTo(membership.get("endDate"), value.toString()));
                        break;
                }
            }
        });

        query.where(predicates.toArray(new Predicate[0]));
        TypedQuery<Membership> typedQuery = entityManager.createQuery(query);

        // Pagination
        int totalRecords = typedQuery.getResultList().size();
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<Membership> resultList = typedQuery.getResultList();
        return new PageImpl<>(resultList, pageable, totalRecords);
    }
}

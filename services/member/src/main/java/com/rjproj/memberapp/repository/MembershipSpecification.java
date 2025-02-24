package com.rjproj.memberapp.repository;

import com.rjproj.memberapp.model.Member;
import com.rjproj.memberapp.model.MemberRole;
import com.rjproj.memberapp.model.Membership;
import com.rjproj.memberapp.model.Role;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;


public class MembershipSpecification {


    public static Specification<Membership> hasOrganizationIdAndMembershipTypeNotNull(UUID organizationId) {
        return (root, query, criteriaBuilder) -> {
            // Filter by organizationId
            Predicate organizationPredicate = criteriaBuilder.equal(root.get("organizationId"), organizationId);

            // Ensure membershipType is not null
            Predicate membershipTypePredicate = criteriaBuilder.isNotNull(root.get("membershipType"));

            // Combine the two predicates
            return criteriaBuilder.and(organizationPredicate, membershipTypePredicate);
        };
    }

    public static Specification<Membership> filterByFirstName(String firstName) {
        return (root, query, criteriaBuilder) -> {
            if (firstName != null && !firstName.isEmpty()) {
                // Filter by first name OR last name (you can choose to combine them differently)
                return criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("member").get("firstName")),
                                "%" + firstName.toLowerCase() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("member").get("lastName")),
                                "%" + firstName.toLowerCase() + "%")
                );
            }
            return null;
        };
    }

    public static Specification<Membership> filterByEmail(String email) {
        return (root, query, criteriaBuilder) -> {
            if (email != null && !email.isEmpty()) {
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("member").get("email")), "%" + email.toLowerCase() + "%");
            }
            return null;
        };
    }

    public static Specification<Membership> filterByCity(String city) {
        return (root, query, criteriaBuilder) -> {
            if (city != null && !city.isEmpty()) {
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("member").get("memberAddress").get("city")), "%" + city.toLowerCase() + "%");
            }
            return null;
        };
    }

    public static Specification<Membership> filterByCountry(String country) {
        return (root, query, criteriaBuilder) -> {
            if (country != null && !country.isEmpty()) {
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("member").get("memberAddress").get("country")), "%" + country.toLowerCase() + "%");
            }
            return null;
        };
    }

    public static Specification<Membership> filterByMembershipStatus(List<String> membershipStatusNames) {
        return (root, query, criteriaBuilder) -> {
            if (membershipStatusNames != null && !membershipStatusNames.isEmpty()) {
                return root.get("membershipStatus").get("name").in(membershipStatusNames);
            }
            return null;
        };
    }


    public static Specification<Membership> filterByMembershipTypes(List<String> membershipTypeNames) {
        return (root, query, criteriaBuilder) -> {
            if (membershipTypeNames != null && !membershipTypeNames.isEmpty()) {
                return root.get("membershipType").get("name").in(membershipTypeNames);
            }
            return null;
        };
    }

    public static Specification<Membership> filterByRoleNames(List<String> roleNames, UUID organizationId) {
        return (root, query, criteriaBuilder) -> {
            if (roleNames != null && !roleNames.isEmpty()) {
                // Create a subquery to filter Memberships based on role names and organizationId
                Subquery<UUID> subquery = query.subquery(UUID.class); // Use UUID instead of Long
                Root<Membership> subRoot = subquery.from(Membership.class);
                Join<Membership, Member> subMemberJoin = subRoot.join("member");
                Join<Member, MemberRole> subRoleJoin = subMemberJoin.join("roles");

                // Select the Membership IDs that match the role names and organizationId
                subquery.select(subRoot.get("membershipId")) // Ensure this is UUID
                        .where(
                                criteriaBuilder.and(
                                        subRoleJoin.get("name").in(roleNames),
                                        criteriaBuilder.equal(subRoot.get("organizationId"), organizationId)
                                )
                        );

                // Filter the main query using the subquery
                return root.get("membershipId").in(subquery);
            }
            return null;
        };
    }




    public static Specification<Membership> filterByStartDateRange(Date startDateFrom, Date startDateTo) {
        return (root, query, criteriaBuilder) -> {
            if (startDateFrom != null && startDateTo != null) {
                return criteriaBuilder.between(root.get("startDate"), startDateFrom, startDateTo);
            }
            return null;
        };
    }

    public static Specification<Membership> filterByEndDateRange(Date endDateFrom, Date endDateTo) {
        return (root, query, criteriaBuilder) -> {
            if (endDateFrom != null && endDateTo != null) {
                return criteriaBuilder.between(root.get("endDate"), endDateFrom, endDateTo);
            }
            return null;
        };
    }

    public static Specification<Membership> combineFilters(Specification<Membership>... specs) {
        Specification<Membership> combined = Specification.where(specs[0]);
        for (int i = 1; i < specs.length; i++) {
            combined = combined.and(specs[i]);
        }
        return combined;
    }

    public static Specification<Membership> combineFiltersWithOr(Specification<Membership>... specs) {
        Specification<Membership> combined = Specification.where(specs[0]);
        for (int i = 1; i < specs.length; i++) {
            combined = combined.or(specs[i]);
        }
        return combined;
    }


    public static Specification<Membership> applySorting(Sort sort, UUID organizationId) {
        return (root, query, criteriaBuilder) -> {
            // Check if sorting is not null or empty
            if (sort == null || sort.isEmpty()) {
                return criteriaBuilder.conjunction(); // No sorting if not specified
            }

            // Join Membership → Member
            Join<Membership, Member> memberJoin = root.join("member", JoinType.LEFT);

            // Join Member → Roles (Many-to-Many relationship)
            SetJoin<Member, Role> roleJoin = memberJoin.joinSet("roles", JoinType.LEFT);

            // Apply filtering by organizationId (on Membership)
            Predicate organizationPredicate = criteriaBuilder.equal(root.get("organizationId"), organizationId);
            // Combine the organizationPredicate with others if needed
            query.where(organizationPredicate);

            // Order list for sorting
            List<Order> orders = new ArrayList<>();

            // Loop through the sort fields and add corresponding orders
            for (Sort.Order order : sort) {
                String property = order.getProperty();

                if ("role.name".equals(property)) {
                    // Sorting by role.name
                    orders.add(order.isAscending() ?
                            criteriaBuilder.asc(roleJoin.get("name")) :
                            criteriaBuilder.desc(roleJoin.get("name")));
                } else if ("member.firstName".equals(property)) {
                    // Sorting by member's first name
                    orders.add(order.isAscending() ?
                            criteriaBuilder.asc(memberJoin.get("firstName")) :
                            criteriaBuilder.desc(memberJoin.get("firstName")));
                } else if ("member.lastName".equals(property)) {
                    // Sorting by member's last name
                    orders.add(order.isAscending() ?
                            criteriaBuilder.asc(memberJoin.get("lastName")) :
                            criteriaBuilder.desc(memberJoin.get("lastName")));
                } else {
                    // Sorting by other Membership attributes
                    orders.add(order.isAscending() ?
                            criteriaBuilder.asc(root.get(property)) :
                            criteriaBuilder.desc(root.get(property)));
                }
            }


            // Select necessary columns in the result
            query.multiselect(
                    root.get("membershipId"),
                    root.get("createdAt"),
                    root.get("endDate"),
                    root.get("member").get("memberId"),
                    root.get("membershipStatus"),
                    root.get("membershipType"),
                    root.get("organizationId"),
                    root.get("startDate"),
                    root.get("updatedAt"),
                    memberJoin.get("firstName"),  // Include sorted columns in SELECT
                    memberJoin.get("lastName"),   // Include sorted columns in SELECT
                    roleJoin.get("name")         // Include sorted columns in SELECT
            );

            // Apply the sorting
            query.orderBy(orders);

            return criteriaBuilder.conjunction(); // Return the final specification
        };
    }








}

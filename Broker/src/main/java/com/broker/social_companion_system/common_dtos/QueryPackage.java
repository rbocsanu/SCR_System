package com.broker.social_companion_system.common_dtos;

import com.broker.social_companion_system.entities.Query;

public record QueryPackage(Query query,
                           String requestedUnitId,
                           String requestingUser,
                           boolean approved
)
        implements Comparable<QueryPackage> {

    @Override
    public int compareTo(QueryPackage other) {
        return query.compareTo(other.query());
    }

}

package operator.dtos;

import operator.entities.Query;

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

package operator.entities;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.NoArgsConstructor;

@JsonTypeName("service")
@NoArgsConstructor
public class ServiceQuery extends Query {

    // Constructor
    public ServiceQuery(String name, int priorityLevel, long issuingTime) {
        super(name, priorityLevel, issuingTime);
    }

}

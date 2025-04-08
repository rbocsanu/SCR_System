package operator.entities;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ServiceQuery.class, name = "service"),
        @JsonSubTypes.Type(value = TaskQuery.class, name = "task")
})
public abstract class Query implements Comparable<Query>, Serializable {

    @Serial
    private static final long serialVersionUID = 370570593557896947L;
    
    // Fields
    protected String name;
    @Setter
    protected int priorityLevel;
    protected long issuingTime;


    public int compareTo( Query other ) {
        if ( priorityLevel > other.priorityLevel ) return -1;
        else if ( priorityLevel < other.priorityLevel ) return 1;
        else {
            // Both have the same priority, sort by issuing time
            return issuingTime > other.issuingTime ? 1 : -1;
        }
    }
}

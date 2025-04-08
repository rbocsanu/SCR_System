package operator.entities;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.NoArgsConstructor;

@JsonTypeName("task")
@NoArgsConstructor
public class TaskQuery extends Query {

    // Fields
    private int interuptionLevel;
    private boolean isApproved;

    // Constructor
    public TaskQuery(String name, int priorityLevel, long issuingTime) {
        super(name, priorityLevel, issuingTime);
    }

    // Mutators
    public void setApproved(boolean approve) {
        isApproved = approve;
    }

    // Accessors
    public int getInteruptionLevel() {
        return interuptionLevel;
    }

    public Boolean isApproved() {
        return isApproved;
    }

}

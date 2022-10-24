package fr.tripnjoy.groups.model.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.tripnjoy.groups.exception.GroupCreationException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Getter
@JsonAutoDetect
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePrivateGroupRequest {
    private String name;
    @Nullable
    private String description;
    private int maxSize;

    @JsonProperty("maxSize")
    public void setMaxSize(int maxSize){
        if (maxSize < 2){
            throw new GroupCreationException("The size of the group should be superior or equal to 2");
        }
        this.maxSize = maxSize;
    }
}

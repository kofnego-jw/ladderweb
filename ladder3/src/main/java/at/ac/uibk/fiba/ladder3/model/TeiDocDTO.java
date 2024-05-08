package at.ac.uibk.fiba.ladder3.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.List;

public class TeiDocDTO {

    public final String poolId;

    public final String createdOn;

    public final String modifiedOn;

    @JacksonXmlElementWrapper(useWrapping = false)
    public final List<TeiDataDTO> data;

    @JsonCreator
    public TeiDocDTO(
            @JsonProperty("poolId") String poolId,
            @JsonProperty("createdOn") String createdOn,
            @JsonProperty("modifiedOn") String modifiedOn,
            @JsonProperty("data") List<TeiDataDTO> data
    ) {
        this.poolId = poolId;
        this.createdOn = createdOn;
        this.modifiedOn = modifiedOn;
        this.data = data;
    }

    @Override
    public String toString() {
        return "TeiDocDTO{" +
                "poolId='" + poolId + '\'' +
                ", createdOn='" + createdOn + '\'' +
                ", modifiedOn='" + modifiedOn + '\'' +
                ", data=" + data +
                '}';
    }
}

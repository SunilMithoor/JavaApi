package com.app.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerPaginationData {

    @JsonProperty("data")
    private List<CustomerData> data;

    @JsonProperty("meta")
    private MetaData meta;
}

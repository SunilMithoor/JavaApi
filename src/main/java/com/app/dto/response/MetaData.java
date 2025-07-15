package com.app.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
@Getter
@Setter
public class MetaData {

    @JsonProperty("page")
    private Integer page;

    @JsonProperty("take")
    private Integer take;

    @JsonProperty("item_count")
    private Integer itemCount;

    @JsonProperty("page_count")
    private Integer pageCount;

    @JsonProperty("has_previous_page")
    private Boolean hasPreviousPage;

    @JsonProperty("has_next_page")
    private Boolean hasNextPage;
}

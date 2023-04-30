package com.example.certificateback.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class AllDTO<T> {

    private int totalCount;

    private List<T> results;

    public AllDTO(List<T> results) {
        this.totalCount = results.size();
        this.results = results;
    }
}

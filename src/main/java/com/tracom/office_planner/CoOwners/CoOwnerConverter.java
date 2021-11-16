package com.tracom.office_planner.CoOwners;

import org.springframework.core.convert.converter.Converter;

public class CoOwnerConverter implements Converter<String, CoOwners> {
    @Override
    public CoOwners convert(String source) {
        return new CoOwners(source);
    }
}

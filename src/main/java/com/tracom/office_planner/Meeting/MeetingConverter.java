package com.tracom.office_planner.Meeting;

import com.tracom.office_planner.RepeatMeetings.RepeatMeetings;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


public class MeetingConverter implements Converter<String, RepeatMeetings> {
    @Override
    public RepeatMeetings convert(String source) {
        return null;
    }
}

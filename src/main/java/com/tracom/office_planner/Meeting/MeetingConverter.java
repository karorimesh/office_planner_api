package com.tracom.office_planner.Meeting;

/* Converter class to persist multiple dates set for the meeting
 */

import com.tracom.office_planner.RepeatMeetings.RepeatMeetings;
import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class MeetingConverter implements Converter<String, RepeatMeetings> {
    @Override
    public RepeatMeetings convert(String source) {
        return new RepeatMeetings(LocalDate.parse(source, DateTimeFormatter.ISO_DATE));
    }
}

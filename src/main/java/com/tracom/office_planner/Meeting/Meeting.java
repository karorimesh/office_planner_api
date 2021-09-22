package com.tracom.office_planner.Meeting;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Entity
@Table(name = "meeting")

public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int meet_id;
    private String meet_name;
    private LocalDateTime meet_start;
    private  LocalDateTime meet_end;
    private int capacity;

    public Meeting(int meet_id, String meet_name, LocalDateTime meet_start, LocalDateTime meet_end, int capacity) {
        this.meet_id = meet_id;
        this.meet_name = meet_name;
        this.meet_start = meet_start;
        this.meet_end = meet_end;
        this.capacity = capacity;
    }

    public Meeting(String meet_name, LocalDateTime meet_start, LocalDateTime meet_end, int capacity) {
        this.meet_name = meet_name;
        this.meet_start = meet_start;
        this.meet_end = meet_end;
        this.capacity = capacity;
    }

    public Meeting() {
    }

    public int getMeet_id() {
        return meet_id;
    }

    public void setMeet_id(int meet_id) {
        this.meet_id = meet_id;
    }

    public String getMeet_name() {
        return meet_name;
    }

    public void setMeet_name(String meet_name) {
        this.meet_name = meet_name;
    }

    public LocalDateTime getMeet_start() {
        return meet_start;
    }

    public void setMeet_start(LocalDateTime meet_start) {
        this.meet_start = meet_start;
    }

    public LocalDateTime getMeet_end() {
        return meet_end;
    }

    public void setMeet_end(LocalDateTime meet_end) {
        this.meet_end = meet_end;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return "Meeting{" +
                "meet_id=" + meet_id +
                ", meet_name='" + meet_name + '\'' +
                ", meet_start=" + meet_start +
                ", meet_end=" + meet_end +
                ", capacity=" + capacity +
                '}';
    }
}

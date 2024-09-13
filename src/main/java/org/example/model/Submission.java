package org.example.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Data
@Builder
@Table(name = "submission")
public class Submission {

    @Id
    @Column(value = "ID")
    private Long id;

    @Column(value = "TASKID")
    private Long taskid;

    @Column(value = "DESCRIPTION")
    private String description;

    @Column(value = "TIMEOFSUBMISSION")
    private LocalDate timeofsubmission;

    @Column(value = "ACCEPTANCE")
    private Boolean acceptance;

    @Column(value = "SUBMITTERID")
    private Integer submitterid;

}
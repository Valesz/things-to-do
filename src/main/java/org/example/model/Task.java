package org.example.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Data
@Builder
@Table(name = "task")
public class Task {

    @Id
    @Column(value = "ID")
    private Long id;

    @Column(value = "NAME")
    private String name;

    @Column(value = "DESCRIPTION")
    private String description;

    @Column(value = "TIMEOFCREATION")
    private LocalDate timeofcreation;

    @Column(value = "MAINTASKID")
    private Long maintaskid;

    @Column(value = "OWNERID")
    private Long ownerid;
}
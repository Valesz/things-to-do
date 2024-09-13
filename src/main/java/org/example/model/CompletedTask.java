package org.example.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table(name = "completedTasks")
public class CompletedTask {

    @Id
    @Column(value = "ID")
    private Long id;

    @Column(value = "USERID")
    private Long userid;

    @Column(value = "TASKID")
    private Long taskid;

}
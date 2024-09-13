package org.example.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table("keywordsForTasks")
public class KeywordsForTasks {

    @Id
    @Column(value = "ID")
    private Long id;

    @Column(value = "TASKID")
    private Long taskid;

    @Column(value = "KEYWORD")
    private String keyword;

}

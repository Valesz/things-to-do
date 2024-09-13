package org.example.repository;

import org.example.model.CompletedTask;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository("completedTasksRepository")
public interface CompletedTasksRepository extends CrudRepository<CompletedTask, Long> {
}

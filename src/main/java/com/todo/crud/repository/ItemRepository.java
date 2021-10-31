package com.todo.crud.repository;

import com.todo.crud.model.Item;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface ItemRepository extends CrudRepository<Item, Long> {
    Optional<Item> findItemByContent(String content);
    Boolean existsByContent(String content);
}

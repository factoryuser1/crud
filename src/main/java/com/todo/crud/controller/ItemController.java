package com.todo.crud.controller;

import com.todo.crud.model.Item;
import com.todo.crud.repository.ItemRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@CrossOrigin
@RestController
@RequestMapping("/api/items")
@ControllerAdvice
public class ItemController {

    //inject the Item Repository using constructor injection
    private final ItemRepository itemTable;

    public ItemController(ItemRepository itemTable) {
        this.itemTable = itemTable;
    }

    @GetMapping
    public Iterable<Item> findAllToDoItems() {
        return itemTable.findAll();
    }

    @GetMapping("/{id}")
    public Item getToDoItemById(@PathVariable Long id) {

        try {
            return itemTable.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("No such Item exit in the database with id# " + id + "!");
//            throw new ItemNotFoundException();
        }
    }

    @PostMapping
    public ResponseEntity<Item> createNewToDoItem(@RequestBody Item item) {

        if (!itemTable.existsByContent(item.getContent())) {
            return new ResponseEntity<>(itemTable.save(item),HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(item, HttpStatus.I_AM_A_TEAPOT);
        }
    }

    @PatchMapping("/{id}")
    public Item updateToDoItem(@PathVariable Long id, @RequestBody Item item) {
        Item curItem = itemTable.findById(id).orElseThrow(() -> new ItemNotFoundException());
        curItem.setContent(item.getContent());
        curItem.setCompleted(item.getCompleted());
        return itemTable.save(curItem);

    }

    @DeleteMapping("/{id}")
    public String deleteToDoItem(@PathVariable Long id) {
        try {
            itemTable.deleteById(id);
            return "Item# " + id + " was deleted successfully!";

        } catch (EmptyResultDataAccessException e) {
            throw new ItemNotFoundException();
        }
    }


    //Exception Handling Framework
    @ExceptionHandler(ItemNotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ResponseEntity<String> returnNotFoundErrorMessageException(ItemNotFoundException e) {
        return new ResponseEntity<>("No such Item exit in the database with this id.", HttpStatus.BAD_REQUEST);
    }

    static class ItemNotFoundException extends NoSuchElementException {
    }
}

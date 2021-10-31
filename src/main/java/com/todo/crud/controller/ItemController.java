package com.todo.crud.controller;

import com.todo.crud.model.Item;
import com.todo.crud.repository.ItemRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@CrossOrigin
@RestController
@RequestMapping("/api/items/")
@ControllerAdvice
public class ItemController {

    //inject the Item Repository using constructor injection
    private final ItemRepository itemTable;

    public ItemController(ItemRepository itemTable) {
        this.itemTable = itemTable;
    }

    @GetMapping("")
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

    @PostMapping("")
    public Item createNewToDoItem(@RequestBody Item item) {
        if (!itemTable.existsByContent(item.getContent())){
            return itemTable.save(item);
        } else{
            return item;
        }
    }

    @PatchMapping("/{id}")
    public Item updateToDoItem(@PathVariable Long id, @RequestBody Item item){

        try{
            Item currentItem = itemTable.findById(id).get();
            currentItem.setContent(item.getContent());
            return itemTable.save(currentItem);

        } catch (NoSuchElementException e){
//            throw new NoSuchElementException("No such Item exit in the database with id# " + id + "!");
            throw new ItemNotFoundException();
        }
    }

    @DeleteMapping("/{id}")
    public void deleteToDoItem(@PathVariable Long id){
        try{
            itemTable.deleteById(id);

        } catch (EmptyResultDataAccessException e){
            throw new ItemNotFoundException();
        }
    }


    //Exception Handling Framework
    @ExceptionHandler(ItemNotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public String returnNotFoundErrorMessageException(ItemNotFoundException e) {
        return "No such Item exit in the database with this id.";
    }

    static class ItemNotFoundException extends NoSuchElementException {
    }
}

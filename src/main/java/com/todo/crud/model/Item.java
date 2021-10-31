package com.todo.crud.model;

import com.fasterxml.jackson.annotation.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "item")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private Boolean completed = false;

    public Item() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}

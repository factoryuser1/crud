package com.todo.crud.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todo.crud.model.Item;
import com.todo.crud.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerTest {

    Item item1 = new Item();
    Item item2 = new Item();
    Item item3 = new Item();

    //setup test data objects and initialize them and commit them to test DB repository object
    Item item4 = new Item();
    Item item5 = new Item();
    Item item6 = new Item();
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private ItemRepository repository;

    @BeforeEach
    @Transactional
    @Rollback
    public void setUp() {
        item1.setContent("test item 1"); item1.setCompleted(true);
        item2.setContent("test item 2"); item2.setCompleted(false);
        item3.setContent("test item 3"); item3.setCompleted(true);
        item4.setContent("test item 4"); item4.setCompleted(false);
        item5.setContent("test item 5"); item5.setCompleted(true);
        item6.setContent("test item 6"); item6.setCompleted(false);

        repository.save(item1);
        repository.save(item2);
        repository.save(item3);
        repository.save(item4);
        repository.save(item5);
        repository.save(item6);
    }

    @Test
    @Transactional
    @Rollback
    public void getListOfItems() throws Exception{
        MockHttpServletRequestBuilder request = get("/api/items/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        ResultActions perform =this.mvc.perform(request);

        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", equalTo(item1.getId().intValue())))
                .andExpect(jsonPath("$[0].content", is(item1.getContent()) ))
                .andExpect(jsonPath("$[0].completed", is(true)))

                .andExpect(jsonPath("$[1].id", equalTo(item2.getId().intValue())))
                .andExpect(jsonPath("$[1].content", is(item2.getContent()) ))
                .andExpect(jsonPath("$[1].completed", is(false)));
    }

    @Test
    @Transactional
    @Rollback
    public void getItemById() throws Exception{
        MockHttpServletRequestBuilder request = get("/api/items/%d".formatted(item3.getId()));

        ResultActions perform = this.mvc.perform(request);

        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(item3.getId().intValue())))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.content", is(item3.getContent())))
                .andExpect(jsonPath("$.completed", is(true)));
    }

    @Test
    @Transactional
    @Rollback
    public void createNewItem() throws Exception{
        var testJsonString = """
                {
                    "content": "Eat Food",
                    "completed": "true"
                }
                """;

        MockHttpServletRequestBuilder request = post("/api/items/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(testJsonString)
                .accept(MediaType.APPLICATION_JSON);

        ResultActions perform = this.mvc.perform(request);

        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.content", is("Eat Food")))
                .andExpect(jsonPath("$.completed", is(true)));
    }

    @Test
    @Transactional
    @Rollback
    public void updateItemById() throws Exception{

        var jsonString = mapper.writeValueAsString(item1);
        MockHttpServletRequestBuilder request = patch("/api/items/%d".formatted(item1.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonString);

        ResultActions perform = this.mvc.perform(request);

        perform.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.content", is("test item 1")))
                .andExpect(jsonPath("$.completed", is(true)));

//        assertTrue(repository.findItemByCompleted(item1.getCompleted()).isPresent());
        assertEquals(repository.count(), 6);
    }

    @Test
    @Transactional
    @Rollback
    public void deleteItemById() throws Exception{
        MockHttpServletRequestBuilder request = delete("/api/items/%d".formatted(item4.getId()));
        Long countBefore = repository.count();

        ResultActions perform = mvc.perform(request);
        Long countAfter = repository.count();

        perform.andExpect(status().isOk());
        assertFalse(repository.findItemByContent(item4.getContent()).isPresent());
        assertTrue((countBefore - 1) == countAfter);
    }

}

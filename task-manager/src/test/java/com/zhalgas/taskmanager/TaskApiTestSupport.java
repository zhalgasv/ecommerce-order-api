package com.zhalgas.taskmanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
abstract class TaskApiTestSupport {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    private long create(String path, String body) throws Exception {
        String response = mvc.perform(post(path).contentType("application/json").content(body))
        .andExpect(status().isCreated()).andExpect(header().exists("Location"))
        .andReturn().getResponse().getContentAsString();
        return json.readTree(response).get("id").asLong();
    }

    @Test void crudFilteringValidationAndConstraints() throws Exception {
        long user = create("/api/users", "{\"name\":\"Alice\",\"email\":\"alice@example.com\"}");
        long category = create("/api/categories", "{\"name\":\"Work\"}");
        long other = create("/api/categories", "{\"name\":\"Home\"}");
        String request = "{\"title\":\"Learn Spring\",\"description\":\"DI\",\"status\":\"TODO\",\"userId\":" + user + ",\"categoryId\":" + category + "}";
        long task = create("/api/tasks", request);
        mvc.perform(get("/api/tasks/" + task)).andExpect(status().isOk()).andExpect(jsonPath("$.title").value("Learn Spring"));
        mvc.perform(get("/api/tasks").param("categoryId", "" + category).param("userId", "" + user)).andExpect(status().isOk()).andExpect(jsonPath("$.content.length()").value(1));
        mvc.perform(get("/api/tasks").param("categoryId", "" + other)).andExpect(status().isOk()).andExpect(jsonPath("$.content.length()").value(0));
        mvc.perform(put("/api/tasks/" + task).contentType("application/json").content(request.replace("TODO", "DONE"))).andExpect(status().isOk());
        mvc.perform(get("/api/tasks/" + task)).andExpect(jsonPath("$.status").value("DONE"));
        mvc.perform(delete("/api/users/" + user)).andExpect(status().isConflict());
        mvc.perform(delete("/api/categories/" + category)).andExpect(status().isConflict());
        mvc.perform(post("/api/users").contentType("application/json").content("{\"name\":\"Other\",\"email\":\"alice@example.com\"}")).andExpect(status().isConflict());
        mvc.perform(post("/api/categories").contentType("application/json").content("{\"name\":\"Work\"}")).andExpect(status().isConflict());
        mvc.perform(post("/api/tasks").contentType("application/json").content(request.replace("Learn Spring", " "))).andExpect(status().isBadRequest());
        mvc.perform(post("/api/tasks").contentType("application/json").content(request.replace("TODO", "INVALID"))).andExpect(status().isBadRequest());
        mvc.perform(post("/api/tasks").contentType("application/json").content(request.replace("\"userId\":" + user, "\"userId\":999999"))).andExpect(status().isNotFound());
        mvc.perform(get("/api/tasks").param("sort", "nonexistent")).andExpect(status().isBadRequest());
        mvc.perform(delete("/api/tasks/" + task)).andExpect(status().isNoContent());
        mvc.perform(get("/api/tasks/" + task)).andExpect(status().isNotFound());
        mvc.perform(put("/api/users/" + user).contentType("application/json").content("{\"name\":\"Bob\",\"email\":\"bob@example.com\"}")).andExpect(status().isOk());
        mvc.perform(get("/api/users/" + user)).andExpect(jsonPath("$.name").value("Bob"));
        mvc.perform(put("/api/categories/" + category).contentType("application/json").content("{\"name\":\"Education\"}")).andExpect(status().isOk());
        mvc.perform(get("/api/categories/" + category)).andExpect(jsonPath("$.name").value("Education"));
        mvc.perform(get("/api/users")).andExpect(status().isOk());
        mvc.perform(get("/api/categories")).andExpect(status().isOk());
        mvc.perform(delete("/api/categories/" + category)).andExpect(status().isNoContent());
        mvc.perform(delete("/api/categories/" + other)).andExpect(status().isNoContent());
        mvc.perform(delete("/api/users/" + user)).andExpect(status().isNoContent());
        mvc.perform(get("/v3/api-docs")).andExpect(status().isOk()).andExpect(jsonPath("$.paths['/api/tasks']").exists());
    }
}

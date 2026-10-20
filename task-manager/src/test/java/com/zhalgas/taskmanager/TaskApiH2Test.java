package com.zhalgas.taskmanager;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:tasks;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.username=sa", "spring.datasource.password="
}
)
class TaskApiH2Test extends TaskApiTestSupport {
}

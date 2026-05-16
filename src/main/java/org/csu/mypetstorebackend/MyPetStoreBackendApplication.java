package org.csu.mypetstorebackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.csu.mypetstorebackend.persistence")
public class MyPetStoreBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyPetStoreBackendApplication.class, args);
    }

}

package com.pet;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.pet.mapper")
public class PetAdoptionApplication {
    public static void main(String[] args) {
        SpringApplication.run(PetAdoptionApplication.class, args);
        System.out.println("========================================");
        System.out.println("  流浪动物救助领养系统启动成功！");
        System.out.println("  接口地址: http://localhost:8080/api");
        System.out.println("========================================");
    }
}

package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
@AllArgsConstructor
public class Student {

    private int Id;
    private String name;
    private int marks;
}

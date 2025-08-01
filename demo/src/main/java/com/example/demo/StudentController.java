package com.example.demo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
public class StudentController {

    public List<Student>students = new ArrayList<>(List.of(

            new Student(1,"John", 90),
            new Student(2,"Jane", 85),
            new Student(3,"Bob", 95)

    ));

    @GetMapping("/csrf-token")
    public CsrfToken getcsrfToken(HttpServletRequest session) {
        return (CsrfToken) session.getAttribute("_csrf");

    }


	@GetMapping("/student")
	public List<Student> getStudent(){
        return students;

    }



	@PostMapping("/student")
	public Student addStudent(@RequestBody Student student){

        students.add(student);
        return student;

    }


	@GetMapping("/student/id")
	public String getStudentById(@PathVariable int id) {

        
        return "student";

    }


}

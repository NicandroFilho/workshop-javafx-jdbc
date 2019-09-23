package model.services;

import model.entities.Department;

import java.util.ArrayList;
import java.util.List;

public class DepartmentService  {

    public List<Department> findAll(){

        //Mocking Data for Test purposes
        List<Department> list= new ArrayList<>();
        list.add(new Department(1, "Books"));
        list.add(new Department(2, "Electronics"));
        list.add(new Department(3, "Cosmetics"));

        return list;
    }
}

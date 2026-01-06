package com.enotes.todo.service;

import com.enotes.todo.entity.ToDo;
import com.enotes.todo.enums.Priority;
import com.enotes.todo.enums.TodoStatus;
import org.springframework.data.jpa.domain.Specification;

public class ToDoSpecification {

    //All these methods you wrote dynamically build SQL queries using the CriteriaBuilder.

    //root-> represent the table col names
    //query-> to build the query
    //criteriaBuilder-> to build the conditions

    public static Specification<ToDo> createdBy(Integer userId){
        return ((root, query, cb) -> {
            if (userId == null) {
                return null;
            }
            return cb.equal(root.get("createdBy"), userId);
        });
    }

    public static Specification<ToDo> isNotDeleted(){
        return (root, query, cb) -> cb.isFalse(root.get("isDeleted"));
    }

    public static Specification<ToDo>titleContains(String title){
        return ((root, query, cb) ->{
            if (title==null || title.trim().isEmpty()){
                return null;
            }
            return  cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%" );
        });
    }

    public static Specification<ToDo> statusIs(TodoStatus status){
        return ((root, query, cb) ->{
            if (status==null){
                return null;
            }
            return  cb.equal(root.get("status"), status );
        });
    }

    public static Specification<ToDo>priorityIs(Priority priority){
        return ((root, query, cb) ->{
            if (priority==null){
                return null;
            }
            return  cb.equal(root.get("priority"), priority );
        });
    }
}

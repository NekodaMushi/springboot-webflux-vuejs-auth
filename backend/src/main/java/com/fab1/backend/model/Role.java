package com.fab1.backend.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "name")
@Table("roles")
public class Role {

    @Id
    private Long id;

    private String name;
    private String description;


    public Role(String name) {
        this.name = name;
    }

    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        return name;
    }
}
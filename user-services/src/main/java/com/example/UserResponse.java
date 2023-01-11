package com.example;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

    private String userName;
    private String name;
    private String email;
    private int age;
}

package com.example;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UserRequest {

    private String userName;
    private String name;
    private String email;
    private int age;
}

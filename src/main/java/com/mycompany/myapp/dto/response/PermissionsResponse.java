package com.mycompany.myapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PermissionsResponse {

    private long id;
    private String name;
    private String description;
}

package com.mycompany.myapp.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailAccountImportResponse {

    private int imported;
    private int skipped;
    private List<String> errors;
}

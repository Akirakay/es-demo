package com.akira.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author akira
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
public class User {

    private String name;

    private Integer age;
}

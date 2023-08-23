package com.wellnr.common.markup;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Simple helper class to hold a value class (which is immutable) as a mutable parent class.
 *
 * @param <T> The type of the actual value.
 */
@Data
@AllArgsConstructor(staticName = "of")
public class Value<T> {

    T data;

}

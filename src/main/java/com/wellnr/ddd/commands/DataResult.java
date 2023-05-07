package com.wellnr.ddd.commands;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;

/**
 * Return any kind of structured data, usually some JSON object tree.
 *
 * @param <T> The actual type of the data.
 */
@Getter
@AllArgsConstructor
@JsonSerialize(using = DataResult.Serializer.class)
public final class DataResult<T> implements CommandResult {

    T data;

    public static <T> DataResult<T> apply(T data) {
        return new DataResult<>(data);
    }

    @SuppressWarnings("rawtypes")
    public static class Serializer extends StdSerializer<DataResult> {

        @SuppressWarnings("unused")
        protected Serializer() {
            super(DataResult.class);
        }

        @Override
        public void serialize(DataResult value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeObject(value.data);
        }

    }

}

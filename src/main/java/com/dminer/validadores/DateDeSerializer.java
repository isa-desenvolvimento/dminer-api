package com.dminer.validadores;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class DateDeSerializer extends StdDeserializer<Timestamp> {

    public DateDeSerializer() {
        super(Timestamp.class);
    }

    @Override
    public Timestamp deserialize(JsonParser p, DeserializationContext ctxt) {
        try {
            String value = p.readValueAs(String.class);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return new Timestamp(df.parse(value).getTime());
        } catch (DateTimeParseException | ParseException | IOException e) {
        }
        return null;
    }

}
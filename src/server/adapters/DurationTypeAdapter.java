package server.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationTypeAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(JsonWriter jsonWriter, Duration value) throws IOException {
        if (value != null) {
            jsonWriter.value(value.toMinutes());
        } else {
            jsonWriter.nullValue();
        }
    }

    @Override
    public Duration read(JsonReader jsonReader) throws IOException {
        try {
            return Duration.ofMinutes(Long.parseLong(jsonReader.nextString()));
        } catch (NumberFormatException | IllegalStateException ex) {
            jsonReader.nextNull();
            return null;
        }
    }
}

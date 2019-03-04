package pl.edu.pwr.wordnetloom.server.business;

import javax.json.bind.adapter.JsonbAdapter;
import java.util.UUID;

public class UuidAdapter implements JsonbAdapter<UUID, String> {

    @Override
    public String adaptToJson(UUID obj) throws Exception {
        return obj.toString();
    }

    @Override
    public UUID adaptFromJson(String obj) throws Exception {
        return UUID.fromString(obj);
    }
}

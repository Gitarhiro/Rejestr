package org.example;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class WejscieWRejestr {

    private final String uzy;
    private final String dat;
    private final String projekt;
    private final int czas;
    private final String opis;
    private final int id;

    public WejscieWRejestr(String uzy, String data, String projekt, int czas, String opis, int id) {
        this.uzy = uzy;
        this.dat = data;
        this.projekt = projekt;
        this.czas = czas;
        this.opis = opis;
        this.id = id;
    }

    public WejscieWRejestr(JsonObject json) {
        this.uzy = json.getString("uzy");
        this.dat = json.getString("dat");
        this.projekt = json.getString("projekt");
        this.czas = json.getInteger("czas");
        this.opis = json.getString("opis");
        this.id = json.getInteger("id");
    }

    public JsonObject toJson() {
        JsonObject json1 = new JsonObject()
                .put("uzy", uzy)
                .put("data", dat)
                .put("projekt",projekt)
                .put("czas",czas)
                .put("opis",opis)
                .put("id", id);
        return json1;
    }

    String returnUzy() {
        return this.uzy;
    }
    String returnData() {return this.dat;}
    String returnProjekt() {return this.projekt;}
    int returnCzas() {
        return this.czas;
    }
    String returnOpis() {return this.opis;}
    int returnId() {
        return this.id;
    }

}

package org.example;


import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.openapi.RouterBuilder;
import io.vertx.ext.web.validation.RequestParameters;
import io.vertx.ext.web.validation.ValidationHandler;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.rxjava3.core.Vertx;
import io.vertx.rxjava3.sqlclient.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.rxjava3.jdbcclient.JDBCPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.jdbcclient.JDBCConnectOptions;

public class MainVerticle extends AbstractVerticle {
    static JDBCPool pool = JDBCPool.pool(
            io.vertx.rxjava3.core.Vertx.newInstance((io.vertx.core.Vertx) App.vertx),
            // configure the connection
            new JDBCConnectOptions()
                    // H2 connection string
                    .setJdbcUrl("jdbc:mysql://localhost:3306/rejestr")
                    // username
                    .setUser("nowy")
                    // password
                    .setPassword("Admin//123"),
            // configure the pool
            new PoolOptions()
                    .setMaxSize(16)
    );

    MySQLConnectOptions connectOptions = new MySQLConnectOptions()
            .setPort(3306)
            .setHost("localhost")
            .setDatabase("rejestr")
            .setUser("nowy")
            .setPassword("Admin//123");
    PoolOptions poolOptions = new PoolOptions()
            .setMaxSize(5);
    MySQLPool client = (MySQLPool) MySQLPool.client(connectOptions, poolOptions);


    public void start(Promise<Void> startPromise) {
        System.out.println("Udało sie!");
        pool.getConnection().doOnSuccess(res->{System.out.println("Udało sie!");}).doOnError(res->{System.out.println("CHUJAXDDDD");});

        client
                .query("SELECT * FROM users WHERE id='julien'")
                .execute();


        Future<RouterBuilder> routerBuilderFuture = RouterBuilder.create(this.vertx, "rejestr.yaml");
        routerBuilderFuture.onSuccess(routerBuilder -> {
            routerBuilder.operation("getRejestr").handler(routingContext -> {
                        JsonArray output = new JsonArray();
                        pool
                                .query("SELECT * FROM rejestr")
                                .execute()
                                .doOnSuccess(rows -> {
                                    for (Row row : rows) {
                                        WejscieWRejestr nowy = new WejscieWRejestr(
                                                row.getString(0)
                                                , row.getString(1)
                                                , row.getString(2)
                                                , row.getInteger(3)
                                                , row.getString(4)
                                                , row.getInteger(5));
                                        System.out.println(nowy.toJson());
                                        output.add(nowy.toJson());
                                    }
                                });
                        routingContext
                                .response()
                                .setStatusCode(200)
                                .putHeader("content-type", "application/json; charset=UTF-8")
                                .end(output.encodePrettily());
                    });
            routerBuilder.operation("wrzucWRejestr").handler(routingContext -> {
                RequestParameters params = routingContext.get(ValidationHandler.REQUEST_CONTEXT_KEY); // <1>
                JsonObject wejscie = params.body().getJsonObject();
                WejscieWRejestr nowy = new WejscieWRejestr(wejscie);
                pool.preparedQuery("INSERT INTO rejestr (uzy, dat, projekt, czas, opis) VALUES (?, ?, ?, ?, ?)")
                        .execute(Tuple.of(nowy.returnUzy(), nowy.returnData(), nowy.returnProjekt(), nowy.returnCzas(), nowy.returnOpis()));
                routingContext
                        .response()
                        .setStatusCode(200)
                        .end(); // <3>
            });
            routerBuilder.operation("szukajPro").handler(routingContext -> {
                RequestParameters params = routingContext.get("parsedParameters"); // (1)
                String projekt = params.pathParameter("projekt").getString(); // (2)
                JsonArray help = new JsonArray();
                pool
                        .preparedQuery("SELECT * FROM rejestr WHERE projekt = ?")
                        .execute(Tuple.of(projekt))
                        .doOnSuccess(rows -> {
                            for (Row row : rows) {
                                WejscieWRejestr nowy = new WejscieWRejestr(
                                        row.getString(0)
                                        , row.getString(1)
                                        , row.getString(2)
                                        , row.getInteger(3)
                                        , row.getString(4)
                                        , row.getInteger(5));
                                help.add(nowy.toJson());
                            }
                        });
                if(help.isEmpty()) {
                    routingContext.fail(404, new Exception("Nie ma takiego projektu"));
                }
                routingContext
                        .response()
                        .setStatusCode(200)
                        .putHeader("content-type", "application/json; charset=UTF-8")
                        .end(help.encodePrettily());
            });
            routerBuilder.operation("szukajUzy").handler(routingContext -> {
                RequestParameters params = routingContext.get("parsedParameters"); // (1)
                String uzy = params.pathParameter("uzy").getString(); // (2)
                JsonArray help = new JsonArray();
                pool
                        .preparedQuery("SELECT * FROM rejestr WHERE uzy = ?")
                        .execute(Tuple.of(uzy))
                        .doOnSuccess(rows -> {
                            for (Row row : rows) {
                                WejscieWRejestr nowy = new WejscieWRejestr(
                                        row.getString(0)
                                        , row.getString(1)
                                        , row.getString(2)
                                        , row.getInteger(3)
                                        , row.getString(4)
                                        , row.getInteger(5));
                                help.add(nowy.toJson());
                            }
                        });
                if(help.isEmpty()) {
                    routingContext.fail(404, new Exception("Nie ma takiego projektu"));
                }
                routingContext
                        .response()
                        .setStatusCode(200)
                        .putHeader("content-type", "application/json; charset=UTF-8")
                        .end(help.encodePrettily());
            });
            routerBuilder.operation("aktualizujRejestr").handler(routingContext -> {
                RequestParameters params = routingContext.get(ValidationHandler.REQUEST_CONTEXT_KEY); // <1>
                JsonObject update = params.body().getJsonObject();
                if(params.pathParameter("id").getString()==null) {
                    routingContext.fail(404, new Exception("Nie ma takiego projektu"));
                }
                pool.preparedQuery("UPDATE rejestr SET ? = ? WHERE id = ?")
                        .execute(Tuple.of(params.pathParameter("Edyt").getString()
                                ,params.pathParameter("Edycja").getString()
                                ,params.pathParameter("id").getString()));
                routingContext
                        .response()
                        .setStatusCode(200)
                        .putHeader("content-type", "text")
                        .end("Zaktualizowano");
            });

            Router router = routerBuilder.createRouter();

            router.route().handler(res -> res.end("Witam w Rejestrze Pracy!\nŚcieżki:\n" +
                    "/rejestr ,get: pokazuje rejestr\n" +
                    "/rejestr ,post: dodaje do rejestru;\n" +
                    "/szukaj/uzytkownik/{nazwa_uzytkownika} ,get: wyszukuje wejść w rejestr na podstawie nazwy użytkownika\n" +
                    "/szukaj/"));

            router.errorHandler(404, routingContext -> { // <2>
                JsonObject errorObject = new JsonObject() // <3>
                        .put("code", 404)
                        .put("message",
                                (routingContext.failure() != null) ?
                                        routingContext.failure().getMessage() :
                                        "Not Found"
                        );
                routingContext
                        .response()
                        .setStatusCode(404)
                        .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .end(errorObject.encode()); // <4>
            });
            router.errorHandler(400, routingContext -> {
                JsonObject errorObject = new JsonObject()
                        .put("code", 400)
                        .put("message",
                                (routingContext.failure() != null) ?
                                        routingContext.failure().getMessage() :
                                        "Validation Exception"
                        );
                routingContext
                        .response()
                        .setStatusCode(400)
                        .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .end(errorObject.encode());
            });

            HttpServer server = vertx.createHttpServer(new HttpServerOptions().setPort(8080).setHost("localhost")); // <5>
            server.requestHandler(router).listen();
            startPromise.complete();
        });
        routerBuilderFuture.onFailure(startPromise::fail);

    }

}

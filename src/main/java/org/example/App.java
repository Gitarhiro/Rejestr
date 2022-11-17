package org.example;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.jdbcclient.JDBCConnectOptions;
import io.vertx.rxjava3.jdbcclient.JDBCPool;
import io.vertx.sqlclient.PoolOptions;

public class App 
{
    static Vertx vertx = Vertx.vertx();

    public static void main( String[] args )
    {

        vertx.deployVerticle(new MainVerticle());


    }
}

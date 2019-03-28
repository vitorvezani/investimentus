package com.investimentus.backend

import com.investimentus.backend.handler.StockHandler
import io.vertx.config.ConfigRetriever
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.StaticHandler

class WebVerticle : AbstractVerticle() {

  override fun start(startFuture: Future<Void>) {
    val router = Router.router(vertx)

    // Serve static resources from the /assets directory
    router.route("/assets/*").handler(StaticHandler.create("assets"))

    router.get("/").handler { index(it) }
    router.get("/stocks/:ticker").handler { StockHandler.getById(it) }
    router.get("/stocks/:ticker/time-series-intraday").handler { StockHandler.getTimeSeriesIntraday(it) }

    // TODO: https://blog.codecentric.de/en/2019/02/vert-x-kotlin-coroutines
    ConfigRetriever.create(vertx).getConfig { json ->
      val port = json.result().getInteger("port")
      vertx
        .createHttpServer()
        .requestHandler(router)
        .listen(port) { http ->
          if (http.succeeded()) {
            startFuture.complete()
            println("HTTP server started on port $port")
          } else {
            startFuture.fail(http.cause())
          }
        }
    }
  }

  private fun index(req: RoutingContext) {
    req.response()
      .putHeader("content-type", "text/plain")
      .end("Hello from Vert.x!")
  }


}

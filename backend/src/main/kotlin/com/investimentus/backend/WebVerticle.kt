package com.investimentus.backend

import com.investimentus.backend.handler.StockHandler
import io.vertx.config.ConfigRetriever
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.kotlin.config.getConfigAwait
import io.vertx.kotlin.core.http.listenAwait
import io.vertx.kotlin.coroutines.CoroutineVerticle


class WebVerticle : CoroutineVerticle() {

  override suspend fun start() {
    val router = Router.router(vertx)

    // Serve static resources from the /assets directory
    router.route("/").handler(StaticHandler.create())

    router.get("/stocks/:ticker").handler { StockHandler.getById(it) }
    router.get("/stocks/:ticker/time-series-intraday").handler { StockHandler.getTimeSeriesIntraday(it) }

    val json = ConfigRetriever.create(vertx).getConfigAwait()
    val port = json.getInteger("port")
    try {
      vertx.createHttpServer().requestHandler(router).listenAwait(port)
      println("HTTP server started on port $port")
    } catch (ex: Exception) {
      error("Could not spawn web server at port $port")
    }
  }

}

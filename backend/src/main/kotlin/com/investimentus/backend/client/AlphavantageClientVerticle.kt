package com.investimentus.backend.client

import io.vertx.config.ConfigRetriever
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.config.getConfigAwait
import io.vertx.kotlin.coroutines.CoroutineVerticle

class AlphavantageClientVerticle : CoroutineVerticle() {

  override suspend fun start() {
    val client = WebClient.create(vertx)

    val json = ConfigRetriever.create(vertx).getConfigAwait()
    val apiKey = json.getJsonObject("alphavantage").getString("api-key")

    vertx.eventBus().consumer<String>("stock.time_series_intraday") { msg ->
      println("I have received a message: ${msg.body()}")
      // Send a GET request
      val uri = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=MSFT&interval=5min&apikey=$apiKey"
      print(uri)
      client.getAbs(uri).send {
        if (it.succeeded()) {
          msg.reply(it.result().body())
        } else {
          println(it.toString())
          msg.fail(404, it.cause().message)
        }
      }
    }
  }
}

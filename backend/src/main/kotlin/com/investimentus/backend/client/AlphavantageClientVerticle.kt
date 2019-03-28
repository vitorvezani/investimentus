package com.investimentus.backend.client

import io.vertx.config.ConfigRetriever
import io.vertx.core.AbstractVerticle
import io.vertx.ext.web.client.WebClient

class AlphavantageClientVerticle : AbstractVerticle() {

  override fun start() {
    val client = WebClient.create(vertx)
    val eb = vertx.eventBus()

    ConfigRetriever.create(vertx).getConfig { json ->
      val apiKey = json.result().getJsonObject("alphavantage").getString("api-key")

      eb.consumer<String>("stock.time_series_intraday") { msg ->
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
}

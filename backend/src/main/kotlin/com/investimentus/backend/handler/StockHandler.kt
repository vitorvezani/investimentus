package com.investimentus.backend.handler

import com.investimentus.backend.event.StockEvent
import com.investimentus.backend.model.Stock
import io.vertx.core.AsyncResult
import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.Message
import io.vertx.core.json.Json
import io.vertx.ext.web.RoutingContext

class StockHandler {
  companion object {
    fun getTimeSeriesIntraday(req: RoutingContext) {
      val ticker = req.request().getParam("ticker")
      val eb = req.vertx().eventBus()
      eb.send("stock.time_series_intraday", Json.encode(StockEvent(ticker))) { event: AsyncResult<Message<Buffer>> ->

        val body = event.result().body()

        if (event.succeeded()) {
          req.response().end(body)
        } else {
          req.response().setStatusCode(500).end(body)
        }
      }
    }

    fun getById(req: RoutingContext) {
      val ticker = req.request().getParam("ticker")
      req.response()
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(Json.encodePrettily(Stock("Itau Unibanco Holding SA", ticker)))
    }
  }
}

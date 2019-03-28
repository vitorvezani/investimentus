package com.investimentus.backend.handler

import com.investimentus.backend.event.StockEvent
import com.investimentus.backend.model.Stock
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.Json
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.eventbus.sendAwait
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class StockHandler {

  companion object {

    fun getTimeSeriesIntraday(req: RoutingContext) {
      val ticker = req.request().getParam("ticker")
      GlobalScope.launch(req.vertx().dispatcher()) {
        try {
          val message = req.vertx().eventBus().sendAwait<Buffer>("stock.time_series_intraday", Json.encode(StockEvent(ticker)))
          req.response().end(message.body())
        } catch (ex: Exception) {
          req.response().setStatusCode(500).end(ex.message)
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

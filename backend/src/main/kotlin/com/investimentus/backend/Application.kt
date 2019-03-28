package com.investimentus.backend

import com.investimentus.backend.client.AlphavantageClientVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx

class Application {
  fun initApp() {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(WebVerticle::class.java.name)
    val options = DeploymentOptions()
    options.isWorker = true
    vertx.deployVerticle(AlphavantageClientVerticle::class.java.name, options)
  }
}

fun main() {
  val app = Application()
  app.initApp()
}

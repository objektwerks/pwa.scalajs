package todo

import org.scalajs.dom.experimental.RequestInfo
import org.scalajs.dom.experimental.serviceworkers.ServiceWorkerGlobalScope._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.util.{Failure, Success}

object ServiceWorker {
  val todoCache = "todo-cache"
  val todoAssets: js.Array[RequestInfo] = List[RequestInfo](
    "/",
    "index.html",
    "style.css",
    "w3c.4.10.css",
    "favicon.ico",
    "logo.png",
    "logo-96.png",
    "logo-128.png",
    "logo-170.png",
    "logo-192.png",
    "logo-256.png",
    "logo-341.png",
    "logo-384.png",
    "logo-512.png",
    "js-opt.js",
    "sharedjs-opt.js",
    "sw-opt.js"
  ).toJSArray

  def toCache(): Unit = {
    self.caches.open(todoCache)
      .toFuture
      .onComplete {
        case Success(cache) => println("toCache: caching assets..."); cache.addAll(todoAssets)
        case Failure(exception) => println(exception)
      }
  }
}
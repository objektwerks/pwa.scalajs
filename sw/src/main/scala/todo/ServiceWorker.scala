package todo

import org.scalajs.dom.experimental.serviceworkers.ExtendableEvent
import org.scalajs.dom.experimental.serviceworkers.ServiceWorkerGlobalScope._
import org.scalajs.dom.experimental.{Request, RequestInfo, Response}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
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

  def toCache(): Future[Unit] = {
    self.caches.open(todoCache)
      .toFuture
      .onComplete {
        case Success(cache) => println("toCache: caching assets..."); cache.addAll(todoAssets)
        case Failure(exception) => println(s"toCache: failed > $exception")
      }
    Future.successful(())
  }

  def fromCache(request: Request): Future[Response] = {
    self.caches.`match`(request)
      .toFuture
      .asInstanceOf[Future[Response]]
      .map { response: Response =>
          println(s"fromCache: matched request > ${request.url}")
          response
      }
  }

  def invalidateCache(): Unit =  {
    self.caches.delete(todoCache)
      .toFuture
      .map { invalidatedCache =>
        println(s"invalidateCache: cache invalidated?', $invalidatedCache")
        if (invalidatedCache) toCache()
      }
    ()
  }

  self.addEventListener("install", (event: ExtendableEvent) => {
    println(s"install: service worker installed > $event")
    event.waitUntil(toCache().toJSPromise)
  })

  self.addEventListener("activate", (event: ExtendableEvent) => {
    println(s"activate: service worker activated > $event")
    invalidateCache()
    self.clients.claim()
  })
}
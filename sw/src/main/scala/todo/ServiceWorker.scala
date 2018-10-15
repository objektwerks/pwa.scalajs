package todo

import org.scalajs.dom.experimental.Fetch._
import org.scalajs.dom.experimental.serviceworkers.ServiceWorkerGlobalScope._
import org.scalajs.dom.experimental.serviceworkers.{ExtendableEvent, FetchEvent}
import org.scalajs.dom.experimental._

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
    "sharedjs-opt.js"
  ).toJSArray

  def main(args: Array[String]): Unit = {
    self.addEventListener("install", (event: ExtendableEvent) => {
      println(s"install: service worker installed > ${event.toString}")
      event.waitUntil(toCache().toJSPromise)
    })

    self.addEventListener("activate", (event: ExtendableEvent) => {
      println(s"activate: service worker activated > ${event.toString}")
      invalidateCache()
      self.clients.claim()
    })

    self.addEventListener("fetch", (event: FetchEvent) => {
      if (event.request.cache == RequestCache.`only-if-cached`
        && event.request.mode != RequestMode.`same-origin`) {
        println(s"fetch: Bug [823392] cache === only-if-cached && mode !== same-orgin' > ${event.request.url}")
      } else {
        fromCache(event.request).onComplete {
          case Success(response) =>
            println(s"fetch: in cache > ${event.request.url}")
            response
          case Failure(error) =>
            println(s"fetch: not in cache, calling server... > ${event.request.url} > ${error.printStackTrace()}")
            fetch(event.request)
              .toFuture
              .onComplete {
                case Success(response) => response
                case Failure(finalError) => println(s"fetch: final fetch failed > ${finalError.printStackTrace()}")
              }
        }
      }
    })

    println("main: ServiceWorker installing...")
  }

  def toCache(): Future[Unit] = {
    self.caches.open(todoCache)
      .toFuture
      .onComplete {
        case Success(cache) =>
          println("toCache: caching assets...")
          cache.addAll(todoAssets).toFuture
        case Failure(error) =>
          println(s"toCache: failed > ${error.printStackTrace()}")
      }
    Future.unit
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
        if (invalidatedCache) {
          println(s"invalidateCache: cache invalidated!', $invalidatedCache")
          toCache()
        }
      }
    ()
  }
}
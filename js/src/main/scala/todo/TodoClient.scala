package todo

import org.scalajs.dom._
import org.scalajs.dom.experimental.serviceworkers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scala.util.{Failure, Success}

@JSExportTopLevel("TodoClient")
object TodoClient {
  @JSExport
  def init(): Unit = {
    registerServiceWorker()
    version()
  }

  def registerServiceWorker(): Unit = {
    toServiceWorkerNavigator(window.navigator)
      .serviceWorker.register("/sw-opt.js")
      .toFuture.onComplete {
      case Success(registration) => registration.update()
      case Failure(exception) => println(exception)
    }
  }

  def version(): Unit = {
    val version = document.getElementById("version")
    val p = document.createElement("p")
    val text = document.createTextNode("V1")
    val node = p.appendChild(text)
    version.appendChild(node)
    ()
  }
}
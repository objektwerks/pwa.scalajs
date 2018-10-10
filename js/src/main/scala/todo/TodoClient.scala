package todo

import org.scalajs.dom._
import org.scalajs.dom.experimental.serviceworkers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scala.util.{Failure, Success}

@JSExportTopLevel("TodoClient")
object TodoClient {
  private val todosUrl = "http://127.0.0.1:7878/api/v1/todos"
  private val todoRestClient = TodoRestClient(todosUrl)
  private val todoModelView = TodoModelView(todoRestClient)

  @JSExport
  def init(): Unit = {
    version()
    registerServiceWorker()
    todoModelView.init()
  }

  def registerServiceWorker(): Unit = {
    toServiceWorkerNavigator(window.navigator)
      .serviceWorker
      .register("/sw-opt.js")
      .toFuture
      .onComplete {
        case Success(registration) => registration.update()
        case Failure(exception) => println(exception)
      }
  }

  def version(): Unit = {
    val div = document.getElementById("version")
    val p = document.createElement("p")
    val text = document.createTextNode("V1")
    val node = p.appendChild(text)
    div.appendChild(node)
    ()
  }
}
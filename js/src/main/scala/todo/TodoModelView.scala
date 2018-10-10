package todo

class TodoModelView(todoRestClient: TodoRestClient) {
  def init(): Unit = {
    assert(todoRestClient.headers.nonEmpty)
  }
}

object TodoModelView {
  def apply(todoRestClient: TodoRestClient): TodoModelView = new TodoModelView(todoRestClient)
}
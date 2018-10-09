package todo

class TodoRestClient(todosUrl: String) {
  assert(todosUrl.nonEmpty)
}

object TodoRestClient {
  val headers = Map("Content-Type" -> "application/json; charset=utf-8", "Accept" -> "application/json")

  def apply(todosUrl: String): TodoRestClient = new TodoRestClient(todosUrl)
}
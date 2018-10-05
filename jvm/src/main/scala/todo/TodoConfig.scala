package todo

import cats.effect.IO
import com.typesafe.config.ConfigFactory
import pureconfig._
import pureconfig.error.ConfigReaderException

object TodoConfig {
  case class ServerConfig(host: String, port: Int)

  case class DatabaseConfig(schema: String, driver: String, url: String, user: String, password: String)

  case class CorsConfig(anyOrigin: Boolean, allowCredentials: Boolean, maxAge: Long, allowedOrigins: Set[String], allowedMethods: Set[String])

  case class Config(server: ServerConfig, database: DatabaseConfig, cors: CorsConfig)

  def load(confFilePath: String): IO[Config] = {
    IO {
      loadConfig[Config](ConfigFactory.load(confFilePath))
    }.flatMap {
      case Left(error) => IO.raiseError[Config](new ConfigReaderException[Config](error))
      case Right(config) => IO.pure(config)
    }
  }
}
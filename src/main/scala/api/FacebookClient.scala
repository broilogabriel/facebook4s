package api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import api.FacebookJsonSerializers._
import cats.implicits._
import config.FacebookConfig.{appSecret, clientId, version}
import config.FacebookConstants._
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import domain.FacebookAppSecretOps._
import domain.FacebookClientIdOps._
import domain.FacebookVersionInstances._
import domain._
import org.f100ded.scalaurlbuilder.URLBuilder
import play.api.libs.json.Reads
import FacebookClient._

import scala.concurrent.Future

class FacebookClient(clientId: FacebookClientId, appSecret: FacebookAppSecret) extends PlayJsonSupport {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  def appAccessToken(): Future[FacebookAccessToken] = {
    appAccessTokenEither() map {
      case Right(facebookAccessToken) => facebookAccessToken
      case Left(facebookError) => throw new RuntimeException(facebookError.error.message)
    }
  }

  def appAccessTokenEither(): Future[Either[FacebookLoginError, FacebookAccessToken]] = {
    val url = URLBuilder(base = host)
      .withPathSegments(version.show, oauthUri)
      .withQueryParameters(
        "client_id"     -> clientId.show,
        "client_secret" -> appSecret.show,
        "grant_type"    -> "client_credentials"
      )

    for {
      response <- Http().singleRequest(HttpRequest(uri = url.toString()))
      accessToken <- response.status  match {
        case StatusCodes.OK                  => parse[FacebookAccessToken](response.entity).map(_ asRight)
        case StatusCodes.BadRequest          => parse[FacebookLoginError](response.entity).map(_ asLeft)
        case StatusCodes.InternalServerError => Future.successful(loginError("Internal server error.") asLeft)
        case _                               => Future.successful(loginError("Unknown exception") asLeft)
      }
    } yield accessToken
  }

  private def parse[T](httpEntity: HttpEntity)(implicit reads: Reads[T]) =
    Unmarshal[HttpEntity](httpEntity.withContentType(ContentTypes.`application/json`)).to[T]
}

object FacebookClient {
  def apply(clientId: FacebookClientId, appSecret: FacebookAppSecret): FacebookClient =
    new FacebookClient(clientId, appSecret)

  def apply(): FacebookClient =
    new FacebookClient(clientId, appSecret)

  def loginError(message: String) = FacebookLoginError(FacebookError(message))
}


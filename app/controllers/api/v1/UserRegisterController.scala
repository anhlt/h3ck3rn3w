package controllers.api.v1

import java.util.UUID
import services.{AuthTokenService, MailService, UserService}
import javax.inject.Inject
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.impl.providers._
import forms.SignUpForm
import models.entities.User
import play.api.i18n.I18nSupport
import play.api.mvc.{
  AbstractController,
  AnyContent,
  ControllerComponents,
  Request
}
import utils.auth.DefaultEnv

import scala.concurrent.{ExecutionContext, Future}
import forms.BaseForm._
import play.api.libs.json.{JsValue, Json, Writes}
import java.{util => ju}

/**
  * The `Sign Up` controller.
  *
  * @param components             The Play controller components.
  * @param silhouette             The Silhouette stack.
  * @param userService            The user service implementation.
  * @param authInfoRepository     The auth info repository implementation.
  * @param authTokenService       The auth token service implementation.
  * @param avatarService          The avatar service implementation.
  * @param passwordHasherRegistry The password hasher registry.
  * @param mailService           The mailer service.
  * @param ex                     The execution context.
  */
class UserRegisterController @Inject()(
    components: ControllerComponents,
    silhouette: Silhouette[DefaultEnv],
    userService: UserService,
    authInfoRepository: AuthInfoRepository,
    authTokenService: AuthTokenService,
    passwordHasherRegistry: PasswordHasherRegistry,
    mailService: MailService
)(implicit ex: ExecutionContext)
    extends AbstractController(components)
    with I18nSupport {

  /**
    * Handles the submitted form.
    *
    * @return The result to display.
    */
  def submit = silhouette.UnsecuredAction.async {
    implicit request: Request[AnyContent] =>
      SignUpForm.form.bindFromRequest.fold(
        formWithError =>
          Future.successful(BadRequest(Json.toJson(formWithError.errors))),
        data => {
          val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
          userService.retrieve(loginInfo).flatMap {
            case Some(user) =>
              Future.successful(Conflict)
            case None =>
              val authInfo = passwordHasherRegistry.current.hash(data.password)
              val user = User(
                id = ju.UUID.randomUUID(),
                loginInfo = loginInfo,
                email = Some(data.email),
                username = Some(data.username),
                nickname = Some(data.nickname),
                activated = false
              )
              for {
                user <- userService.save(user)
                authInfo <- authInfoRepository.add(loginInfo, authInfo)
                authToken <- authTokenService.create(user.id)
              } yield {
                val route =
                  controllers.routes.ActivateAccountController
                    .activate(authToken.token)
                mailService
                  .sendActivateAccountEmail(data.email, route.absoluteURL())
                silhouette.env.eventBus.publish(SignUpEvent(user, request))
                Ok(Json.obj("message" -> "Signed out"))

              }
          }
        }
      )
  }
}

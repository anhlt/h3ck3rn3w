package db
import org.joda.time.DateTime
import db.base.Entity

case class DBUser(
    id: Option[Long],
    email: String,
    updatedAt: DateTime = new DateTime(),
    createdAt: DateTime = new DateTime()
) extends Entity

case class DBOAuthAccessToken(
    id: Option[Long],
    accountId: Long,
    oauthClientId: Long,
    accessToken: String,
    refreshToken: String,
    updatedAt: DateTime,
    createdAt: DateTime
) extends Entity

case class DBOauthAuthorizationCode(
    id: Option[Long],
    accountId: Long,
    oauthClientId: Long,
    code: String,
    redirectUri: Option[String],
    updatedAt: DateTime,
    createdAt: DateTime
) extends Entity

case class DBOauthClient(
    id: Option[Long],
    ownerId: Long,
    grantType: String,
    clientId: String,
    clientSecret: String,
    redirectUri: Option[String],
    updatedAt: DateTime,
    createdAt: DateTime
) extends Entity

case class DBLoginInfo(
    id: Option[Long],
    providerID: String,
    providerKey: String,
    updatedAt: DateTime = new DateTime(),
    createdAt: DateTime = new DateTime()
) extends Entity

case class DBUserLoginInfo(
    id: Option[Long],
    userID: Long,
    loginInfoId: Long,
    updatedAt: DateTime = new DateTime(),
    createdAt: DateTime = new DateTime()
) extends Entity


case class DBPasswordInfo(
    hasher: String,
    password: String,
    salt: Option[String],
    loginInfoId: Long,
    updatedAt: DateTime = new DateTime(),
    createdAt: DateTime = new DateTime()
) extends Entity

case class DBOAuth1Info(
    id: Option[Long],
    token: String,
    secret: String,
    loginInfoId: Long,
    updatedAt: DateTime,
    createdAt: DateTime
) extends Entity

case class DBOAuth2Info(
    id: Option[Long],
    accessToken: String,
    tokenType: Option[String],
    expiresIn: Option[Int],
    refreshToken: Option[String],
    loginInfoId: Long,
    updatedAt: DateTime,
    createdAt: DateTime
) extends Entity
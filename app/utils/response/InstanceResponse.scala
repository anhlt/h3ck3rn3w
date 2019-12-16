package utils.response
import play.api.libs.json._
import models.entities.User

object JsonWriters {
  implicit val flowerMeaningInfoNative2Json = Json.writes[models.entities.User]
  implicit def searchResultsWrites[T](
      implicit fmt: Writes[T]
  ): Writes[PagingResponse[T]] = new Writes[PagingResponse[T]] {
    def writes(ts: PagingResponse[T]) =
      JsObject(
        Seq(
          "page" -> JsNumber(ts.page),
          "pageSize" -> JsNumber(ts.pageSize),
          "total" -> JsNumber(ts.total),
          "elements" -> JsArray(ts.elements.map(Json.toJson(_)))
        )
      )
  }

  implicit def InstanceResponeWrites[T](
      implicit fmt: Writes[T]
  ): Writes[InstanceRespone[T]] = new Writes[InstanceRespone[T]] {
    def writes(ts: InstanceRespone[T]) =
      JsObject(
        Seq(
          "data" -> Json.toJson(ts.data)
        )
      )
  }

}

case class InstanceRespone[T](
    data: T
)

case class PagingResponse[T](
    elements: Seq[T],
    page: Int,
    pageSize: Int,
    total: Int
)

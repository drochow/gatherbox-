package de.beuth.linkedin.scanner.impl

import java.time.Instant
import java.time.temporal.ChronoUnit

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, PersistentEntity}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import de.beuth.scanner.commons.{ProfileScanSerializerRegistry, ProfileScannerEntity, ProfileScannerState}
import play.api.libs.json._
import de.beuth.utils.JsonFormats.singletonFormat
import de.beuth.utils.ProfileLink

import scala.collection.immutable.Seq

/**
  * Perstence Entity for a profile scan indexed by keyword
  */
class LinkedinScannerEntity extends ProfileScannerEntity {

  override def behavior: Behavior =
    scanStatusBehavior.orElse(profileBehavior).orElse(getProfiles)
}


object LinkedinScanSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq() ++ ProfileScanSerializerRegistry.serializers
}
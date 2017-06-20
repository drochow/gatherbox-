package de.beuth.gatherbox.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaClientComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import de.beuth.gatherbox.api.GatherboxService
import com.softwaremill.macwire._
import de.beuth.scan.api.ScanService

class GatherboxLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new GatherboxApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new GatherboxApplication(context) with LagomDevModeComponents

  override def describeServices = List(
    readDescriptor[GatherboxService]
  )
}

abstract class GatherboxApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with CassandraPersistenceComponents
    with AhcWSComponents
    with LagomKafkaClientComponents
{

  lazy val scanService = serviceClient.implement[ScanService]

  // Bind the services that this server provides
  override lazy val lagomServer = LagomServer.forServices(
    bindService[GatherboxService].to(wire[GatherboxServiceImpl])
  )

  // Register the JSON serializer registry
  override lazy val jsonSerializerRegistry = GatherboxSerializerRegistry

  // Register the gatherbox persistent entity
  persistentEntityRegistry.register(wire[GatherboxEntity])
}
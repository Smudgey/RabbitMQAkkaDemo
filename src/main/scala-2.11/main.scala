/*
/**
  * Created by Luke on 08/08/2016.
  */
import akka.actor.{ActorRef, ActorSystem}
import com.thenewmotion.akka.rabbitmq._
import concurrent.duration._

class main extends App {
  /* Here we're creating an ActorSystem - this handles all the existing
  Actors within the scope of our applications and their respective lifecycles.
  All Actors are in some way implicitly linked/related to the overarching ActorSystem.
  The ActorSystem provides us with ActorReferences to the Actors we create - this is useful
  as it allows us for the application to be reactive.
  * */
  val system = ActorSystem("helloakka")

  /*
  * Here we're accessing the ConnectionFactory to provide us with a ConnectionActor. This basically
  * acts as a means of managing communications over a 'channel' between Actors wanting to message each other.
  *
  * In Akka this is known in a more general sense as a Supervisor - every Actor has a Supervisor
  * associated with it that effectively acts as an error handling mechanism (e.g when the connection is lost
  * the Supervisor will specify how to handle this).
  *
  * This is specific to the wrapper library we're using.
  * */
  val factory = new ConnectionFactory()
  val connectionActor: ActorRef = system.actorOf(ConnectionActor.props(factory))

  /*
  * Here we're telling the ActorSystem that we want to have the factory associated with the
  * connection actor we've just created reconnect ever 10 seconds in the instance of the connection
  * becoming lost.
  * */
  system.actorOf(ConnectionActor.props(factory, reconnectionDelay = 10.seconds), "my-connection")

  /*
  * Here we're associating the ConnectionActor with a channel through which it can asyncrhousnly send messages
  * through the 'bang' operator (!) - this is an alias of the 'tell' method. Effectively this allows us
  * to send a message through a communication channel without having to wait for the recipient to
  * appropriately handle/process the message - it's just shoved on to the queue of the recipient.
  * */
  connectionActor ! CreateChannel(ChannelActor.props(), Some("akkaDemoChannel"))

  /*
  * Here we're creating a channel associated with the connectionActor that will have a messaging
  * queue stored in the RabbitMQ server (see setupChannel method)
  * */
  val channelActor: ActorRef = connectionActor.createChannel(ChannelActor.props(setupChannel))
  /*
  * We're passing to the channelActor through the bang method its ChannelMessage function. This is weird but
  * works. See publish method.
  * */
  channelActor ! ChannelMessage(publish)
}

def setupChannel(channel: Channel, self: ActorRef) {
  channel.queueDeclare("queue_name", false, false, false, null)
}

def publish(channel: Channel) {
  channel.basicPublish("", "queue_name", null, "Hello world".getBytes)
}
*/

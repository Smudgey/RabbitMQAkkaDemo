import akka.actor.{ActorRef, ActorSystem}
import com.thenewmotion.akka.rabbitmq._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object PublishSubscribe extends App {
  implicit val system = ActorSystem()
  val HOST_ADDRESS: String = "172.17.25.68"
  val USER_NAME: String = "jesus"
  val USER_PASSWORD: String = "jesus"

  val factory = new ConnectionFactory()
  factory.setHost(HOST_ADDRESS)
  factory.setUsername(USER_NAME)
  factory.setPassword(USER_PASSWORD)
  val connection = system.actorOf(ConnectionActor.props(factory), "rabbitmq")
  val exchange = "amq.fanout"


  def setupPublisher(channel: Channel, self: ActorRef) {
    val queue = channel.queueDeclare().getQueue
    channel.queueBind(queue, exchange, "")
  }
  connection ! CreateChannel(ChannelActor.props(setupPublisher), Some("publisher"))


  /*def setupSubscriber(channel: Channel, self: ActorRef) {
    val queue = channel.queueDeclare().getQueue
    channel.queueBind(queue, exchange, "")
    val consumer = new DefaultConsumer(channel) {
      override def handleDelivery(consumerTag: String, envelope: Envelope, properties: BasicProperties, body: Array[Byte]) {
        println("received: " + fromBytes(body))
      }
    }
    channel.basicConsume(queue, true, consumer)
  }
  connection ! CreateChannel(ChannelActor.props(setupSubscriber), Some("subscriber"))*/


  Future {
    def loop(n: Long) {
      val publisher = system.actorSelection("/user/rabbitmq/publisher")

      def publish(channel: Channel) {
        channel.basicPublish(exchange, "", null, toBytes(n))
      }
      publisher ! ChannelMessage(publish, dropIfNoChannel = false)

      Thread.sleep(1000)
      loop(n + 1)
    }
    loop(0)
  }

  def fromBytes(x: Array[Byte]) = new String(x, "UTF-8")
  def toBytes(x: Long) = x.toString.getBytes("UTF-8")
}
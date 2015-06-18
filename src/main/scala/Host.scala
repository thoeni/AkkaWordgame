import akka.actor.{ActorRef, Props, ActorSystem, Actor}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.Await

class Host extends Actor {
  val hiddenPhrase = "hello world"
  var zippedPhrase = hiddenPhrase.zip(hiddenPhrase.map(c => if (c != ' ') '_' else c))
  override def receive: Receive = {

    case Guess(c) => {
      zippedPhrase = zippedPhrase.map(pair => if (pair._1 == c) (pair._1, pair._1) else pair)
      sender ! Phrase(zippedPhrase.map(_._2).mkString)
    }
  }
}

case class Guess(character: Char)

case class Phrase(phrase: String)

class Game {
  implicit val timeout = Timeout(5 seconds)
  val system = ActorSystem("OurGame")
  private val hostActor: ActorRef = system.actorOf(Props[Host])

  def guess(guessChar: Char): String = {
    val Phrase(response) = Await.result(hostActor ? Guess(guessChar), 5 seconds)
    response
  }
}
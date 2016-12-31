package controllers

import java.io.File
import javax.inject._

import play.api.mvc._

import scala.collection.mutable
import scala.io
import scala.io.Source
import scala.util.Random

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() extends Controller {

  val games = new GamesManager(new SubjectChooser(new File("conf/categories.csv")))

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def createGame() = play.mvc.Results.TODO

  def showSubject(game: String, user: String) = play.mvc.Results.TODO

  def resetGame(game: String) = play.mvc.Results.TODO
}

class GamesManager(subjectChooser: SubjectChooser) {
  private val games = mutable.Map[String, Game]()

  def createGame(name: String, players: List[User]): Game = {
    val newGame = Game(name, players, subjectChooser.choose(), selectFake(players))
    games(name) = newGame
    newGame
  }

  def getGame(name: String): Game = {
    games(name)
  }

  def resetGame(name: String): Game = {
    val existingGame = games(name)
    val updatedGame = existingGame.copy( subject = subjectChooser.choose(), fakeArtist = selectFake(existingGame.players) )
    games(name) = updatedGame
    updatedGame
  }

  private def selectFake(players: List[User]) = players( Random.nextInt(players.length) )

}

class SubjectChooser(csv: File) {
  private val subjects: List[Subject] = readFile(csv)

  private def readFile(csv: File) = {
    (for (line <- Source.fromFile(csv)(io.Codec.UTF8).getLines()) yield {
      line.split(",", 2) match {
        case Array(word, category) => Subject(word, category)
      }
    }).toList
  }

  def choose(): Subject = {
    subjects( Random.nextInt(subjects.length) )
  }

}

case class User(name: String)
case class Game(name: String, players: List[User], subject: Subject, fakeArtist: User)
case class Subject(word: String, category: String)
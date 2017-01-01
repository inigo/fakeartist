package controllers

import java.io.{File, InputStream}
import javax.inject._

import play.api.mvc._

import scala.collection.mutable
import scala.io
import scala.io.Source
import scala.util.Random

@Singleton
class HomeController @Inject() extends Controller {

  val games = new GamesManager(new SubjectChooser(this.getClass.getResourceAsStream("/categories.csv")))


  def index = Action {
    val allGames = games.listGames()
    Ok(views.html.index(allGames))
  }

  //noinspection TypeAnnotation
  def createGame() = Action(parse.urlFormEncoded) { request =>
    val name = request.body("name").head
    val playersText = request.body("players").head
    val players = playersText.split("\n").map(_.trim).filterNot(_.isEmpty).map(User).toList

    val newGame = games.createGame(name.trim, players)

    Redirect(routes.HomeController.showGame(newGame.name))
  }

  def showGame(name: String) = Action {
    val game = games.getGame(name)
    Ok(views.html.showGame(game))
  }

  def showSubject(name: String, userName: String) = Action {
    val game = games.getGame(name)
    val user = User(userName)
    val isFake = game.fakeArtist == user

    Ok(views.html.showSubject(game, user, isFake))
  }

  def resetGame(name: String) = Action {
    val newGame = games.resetGame(name)
    Redirect(routes.HomeController.showGame(newGame.name))
  }

  def about() = Action {
    Ok(views.html.about())
  }
}

class GamesManager(subjectChooser: SubjectChooser) {
  private val games = mutable.Map[String, Game]()

  def listGames(): List[Game] = games.values.toList

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

class SubjectChooser(csv: InputStream) {
  private val subjects: List[Subject] = readFile(csv)

  private def readFile(csv: InputStream) = {
    (for (line <- Source.fromInputStream(csv)(io.Codec.UTF8).getLines()) yield {
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
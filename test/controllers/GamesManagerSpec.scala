package controllers

import org.specs2.mutable.Specification

class GamesManagerSpec extends Specification {

  lazy val manager = new GamesManager(new SubjectChooser(this.getClass.getResourceAsStream("/categories.csv")))
  val users = List(User("red fish"), User("blue fish"))

  "managing games" should {
    "create a new game" in {
      val game = manager.createGame("test", users)
      game.fakeArtist must be oneOf(users : _* )
      game.subject.word must haveLength(greaterThan(0))
    }
    "allow a new game to be retrieved" in {
      val game = manager.createGame("test2", users)
      manager.getGame("test2") mustEqual game
    }
  }

}

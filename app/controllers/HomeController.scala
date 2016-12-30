package controllers

import java.io.File
import javax.inject._

import play.api.mvc._

import scala.io
import scala.io.Source
import scala.util.Random

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() extends Controller {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

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

case class Subject(word: String, category: String)
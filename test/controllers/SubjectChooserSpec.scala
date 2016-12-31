package controllers

import org.specs2.mutable.Specification

class SubjectChooserSpec extends Specification {

  private lazy val chooser = new SubjectChooser(this.getClass.getResourceAsStream("/categories.csv"))

  "choosing a subject" should {
    "return a word and category" in {
      val subject = chooser.choose()
      subject.word must haveLength(greaterThan(0))
      subject.category must haveLength(greaterThan(0))
    }
    "return different subjects most of the time" in {
      val (s1, s2, s3, s4) = (chooser.choose(), chooser.choose(), chooser.choose(), chooser.choose())
      Set(s1, s2, s3, s4) must haveLength(greaterThan(2))
    }
  }

}

package org.scaladownunder

class ScalaDeveloper extends WillingnessTo(Set(Learn(), UnLearn()))
with FunctionalProgrammer
with CanLearnFromMistakes {

  def quirk = ???
}

trait FunctionalProgrammer {
  def quirk: Any
}
class WillingnessTo(skills: Set[Skill])
trait CanLearnFromMistakes
trait Skill
case class Learn() extends Skill
case class UnLearn() extends Skill
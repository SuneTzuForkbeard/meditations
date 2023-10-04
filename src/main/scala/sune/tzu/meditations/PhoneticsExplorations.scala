package sune.tzu.meditations

import org.apache.commons.codec.language.Soundex
import sune.tzu.meditations.phonetics.IPAConverter
import sune.tzu.meditations.phonetics.IPAConverter.IPAPhoneme

object PhoneticsExplorations {

  def main(args: Array[String]): Unit = {
    val result = IPAConverter.translate(
      """
        |Welcome back to the meditation series that focuses on increasing the practitioners daily agility and influence.
        |		Today, we will be taking our explorations into the area surrounding your happy place. Up until now, you have done a good job at clearing the obstacles we've encountered along the way using the correct measure of force, and you deserve a break from the killings. This meditation will contain a minimum of violence, but you are in for quite a ride nonetheless.
        |        You can perform this meditation either sitting or lying down. Both will do you good.
        |
        |
        |""".stripMargin)

    val asInput = result
      .collect {
        case ipa @ IPAPhoneme(_,_) => ipa
      }
      .map(res => s"""<phoneme alphabet="ipa" ph="${res.phStr}">${res.inputWord}</phoneme>""")
      .mkString("\r\n")

    println(asInput)

  }

}

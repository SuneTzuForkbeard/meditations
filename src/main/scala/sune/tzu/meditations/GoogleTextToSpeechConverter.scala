package sune.tzu.meditations

import com.google.auth.oauth2.{AccessToken, GoogleCredentials}
import com.google.cloud.texttospeech.v1._

import java.io.{File, FileOutputStream}
import scala.io.Source

object GoogleTextToSpeechConverter {

  private val dataFolder = new File("""C:\Users\sune_\OneDrive\Dokumenter\dharma\meditations\Chapter02-ThroughTheFields""")
  private val fileNamePrefix = "Chapter 02-Through the fields"
  private val inputFile = new File(dataFolder, s"$fileNamePrefix.txt")
  private val outputFileName = s"$fileNamePrefix-${System.currentTimeMillis()}.mp3"

  def main(args: Array[String]): Unit = {
    val spokenParts = SsmlParser.parse(inputFile)
    spokenParts.foreach(println)



  }

}

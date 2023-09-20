package sune.tzu.meditations

import org.apache.commons.io.FileUtils

import java.io.{File}

object GoogleTextToSpeechConverter {

  private val dataFolder = new File("""C:\Users\sune_\OneDrive\Dokumenter\dharma\meditations\Chapter02-ThroughTheFields""")
  private val fileNamePrefix = "Chapter 02-Through the fields"
  private val inputFile = new File(dataFolder, s"$fileNamePrefix.txt")

  def main(args: Array[String]): Unit = {
    val inputFileText = FileUtils.readFileToString(inputFile, "UTF-8")
    val spokenParts = SsmlParser.parse(inputFile)
    GoogleAPI.generateAudio(spokenParts) match {
      case Left(errs) => {
        println("Following errors occurred:")
        errs.foreach(err => println(s" $err"))
      }
      case Right(res) => {
        FileExporter.exportData(inputFile.getName, inputFileText, res, dataFolder)
      }
    }



  }

}

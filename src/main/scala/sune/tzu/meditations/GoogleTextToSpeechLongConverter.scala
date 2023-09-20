package sune.tzu.meditations

import com.google.cloud.texttospeech.v1._

import java.io.{File, FileOutputStream}
import scala.io.Source

class GoogleTextToSpeechLongConverter {

  private val dataFolder = new File("""C:\Users\sune_\OneDrive\Dokumenter\gonzo\meditations""")
  private val fileNamePrefix = "Chapter 01-The Garden"
  private val outputFileName = s"$fileNamePrefix-${System.currentTimeMillis()}.wav"

  def main(args: Array[String]): Unit = {


    val inputText = Source.fromFile(new File(dataFolder, s"$fileNamePrefix.txt"))
      .getLines
      .mkString("\r\n")

    val client = TextToSpeechLongAudioSynthesizeClient
      .create()
    val input = SynthesisInput.newBuilder()
      .setSsml(inputText)
      .build()

    var voice = VoiceSelectionParams.newBuilder()
      .setLanguageCode("en-US")
      .setName("en-US-Neural2-C")
      .build()

    var audioConfig = AudioConfig.newBuilder()
      .setAudioEncoding(AudioEncoding.LINEAR16)
      .build()

    val request = SynthesizeLongAudioRequest.newBuilder()
      .setParent("projects/hale-brook-399217/locations/europe-north1")
      .setOutputGcsUri(s"gs://sune-tzu-meditations/${outputFileName}")
      .setInput(input)
      .setVoice(voice)
      .setAudioConfig(audioConfig)
      .build()

    val response = client.synthesizeLongAudioAsync(request).get()
    val outStream = new FileOutputStream(new File(dataFolder, outputFileName))
    response.writeTo(outStream)
    outStream.close()

  }

}




package sune.tzu.meditations

import com.google.cloud.texttospeech.v1.{AudioConfig, AudioEncoding, SynthesisInput, TextToSpeechClient, VoiceSelectionParams}
import sune.tzu.meditations.model.{GeneratedAudio, SpokenPart, VoiceDefinitions}

import java.io.{ByteArrayOutputStream, File, FileOutputStream}
import scala.util.{Failure, Success, Try}

object GoogleAPI {

  def generateAudio(parts: Seq[SpokenPart]): Either[Seq[String], Seq[GeneratedAudio]] = validate(parts) match {
    case Some(errs) => Left(errs)
    case _ => {
      val client = TextToSpeechClient
        .create()

      val generationResults = parts.map(part => (part, generateAudio(part, client)))
      val generationErrors = generationResults collect {
        case (part, Failure(err)) => s"Error in generation of part: ${part.partName} ${err.getMessage}"
      }
      if(!generationErrors.isEmpty)
        Left(generationErrors)
      else Right(generationResults.collect {
        case (part, Success(SsmlAndAudio(ssml,bytes))) => GeneratedAudio(part, ssml, bytes)
      })
    }
  }

  /*val


    val response = client.synthesizeSpeech(input, voice, audioConfig)*/


  def generateAudio(part: SpokenPart, client: TextToSpeechClient) = Try {
    val ssml = produceSsml(part)
    val input = SynthesisInput.newBuilder()
      .setSsml(ssml)
      .build()
    val voiceDef = VoiceDefinitions.voiceMap(part.spokenBy)

    val voice = VoiceSelectionParams.newBuilder()
      .setLanguageCode(voiceDef.language)
      .setName(voiceDef.googleId)
      .build()

    var audioConfigBuilder = AudioConfig.newBuilder()
      .setAudioEncoding(AudioEncoding.MP3)

    voiceDef.pitch.foreach(pitch => audioConfigBuilder = audioConfigBuilder.setPitch(pitch))
    voiceDef.speed.foreach(speed => audioConfigBuilder = audioConfigBuilder.setSpeakingRate(speed))

    val audioConfig = audioConfigBuilder.build()

    val response = client.synthesizeSpeech(input, voice, audioConfig)
    val baStream = new ByteArrayOutputStream()
    response.writeTo(baStream)
    baStream.close()
    SsmlAndAudio(ssml,baStream.toByteArray)
  }


  def validate(parts: Seq[SpokenPart]): Option[Seq[String]] = {
    val nameErrors = validateUniqueNames(parts.toList)
    val voiceErrors = parts.map(validateVoice) collect {
      case Some(err) => err
    }
    if (nameErrors.isEmpty && voiceErrors.isEmpty)
      None
    else Some(nameErrors.distinct ++ voiceErrors.distinct)
  }

  def validateUniqueNames(parts: List[SpokenPart]): Seq[String] = parts match {
    case Nil => Seq.empty
    case car :: cldr if cldr.exists(_.partName == car.partName) => car.partName +: validateUniqueNames(cldr)
    case car :: cldr => validateUniqueNames(cldr)
  }

  def validateVoice(part: SpokenPart) = part match {
    case SpokenPart(spBy, _, _,_) if !VoiceDefinitions.voiceMap.contains(spBy) => Some(s"Voice with name: ${spBy} is not defined")
    case _ => None
  }


  def produceSsml(part: SpokenPart): String =
    s"""<speak>
       |  ${part.partsToSend.map(_.ssml).mkString("\r\n")}
       |</speak>
       |""".stripMargin

  case class SsmlAndAudio(ssml : String, audio : Array[Byte])


}

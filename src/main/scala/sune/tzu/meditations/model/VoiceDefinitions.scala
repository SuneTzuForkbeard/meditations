package sune.tzu.meditations.model

object VoiceDefinitions {

  val allVoices = List(
    VoiceDefinition(name= "narrator", googleId = "en-US-Neural2-C", language = "en-US"),
    VoiceDefinition(name= "junkie", googleId = "en-AU-Neural2-D", language = "en-AU", pitch = Some(1.1), speed = Some(1.5)),
    VoiceDefinition(name= "mujer", googleId = "es-ES-Neural2-D", language = "en-US", pitch = Some(0.8), speed = Some(0.7))
  )

  val voiceMap = allVoices
    .map(voice => (voice.name, voice))
    .toMap



  case class VoiceDefinition(
                            name : String,
                            googleId : String,
                            language: String,
                            pitch : Option[Double] = None,
                            speed : Option[Double] = None
                            );
}

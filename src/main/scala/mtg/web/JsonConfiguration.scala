package mtg.web

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.{JsonSerializer, ObjectMapper, SerializerProvider}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import mtg.core.ManaType
import mtg.core.colors.Color
import org.springframework.context.annotation.{Bean, Configuration, Primary}

@Configuration
class JsonConfiguration {
  class ColorSerializer extends JsonSerializer[Color] {
    override def serialize(color: Color, gen: JsonGenerator, serializers: SerializerProvider): Unit = {
      gen.writeString(color.letter)
    }
  }
  class ManaTypeSerializer extends JsonSerializer[ManaType] {
    override def serialize(manaType: ManaType, gen: JsonGenerator, serializers: SerializerProvider): Unit = {
      gen.writeString(manaType.letter)
    }
  }

  @Bean @Primary
  def objectMapper: ObjectMapper = {
    val serializerModule = new SimpleModule()
      .addSerializer(classOf[Color], new ColorSerializer)
      .addSerializer(classOf[ManaType], new ManaTypeSerializer)
    new ObjectMapper()
      .registerModule(DefaultScalaModule)
      .registerModule(serializerModule)
  }
}

package mtg.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.springframework.context.annotation.{Bean, Configuration, Primary}
import org.springframework.web.servlet.config.annotation.{PathMatchConfigurer, WebMvcConfigurer}

@Configuration
class WebMvcConfiguration extends WebMvcConfigurer {
  @Bean @Primary
  def objectMapper: ObjectMapper = {
    new ObjectMapper().registerModule(DefaultScalaModule)
  }
}

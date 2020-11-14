package pro.komdosh.tmsc.stream.video.config

import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.web.reactive.config.WebFluxConfigurer
import pro.komdosh.tmsc.stream.video.ResourceRegionMessageWriter

@Configuration
class WebFluxCodec : WebFluxConfigurer {

    override fun configureHttpMessageCodecs(configurer: ServerCodecConfigurer) {
        configurer.customCodecs().register(ResourceRegionMessageWriter())
    }

}

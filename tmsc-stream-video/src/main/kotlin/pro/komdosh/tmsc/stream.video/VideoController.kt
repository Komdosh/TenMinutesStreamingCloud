package pro.komdosh.tmsc.stream.video

import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.core.io.support.ResourceRegion
import org.springframework.http.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import java.lang.Long.min

@RestController
class VideoController(@Value("classpath:video/coin.mp4") private val video: Resource) {

    @GetMapping("/videos/full")
    fun getFullVideo(@RequestHeader headers: HttpHeaders): ResponseEntity<Resource> {
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
            .contentType(
                MediaTypeFactory.getMediaType(video).orElse(MediaType.APPLICATION_OCTET_STREAM)
            )
            .body(video)
    }

    @GetMapping("/videos")
    fun getVideo(@RequestHeader headers: HttpHeaders): ResponseEntity<ResourceRegion> {
        val region = resourceRegion(video, headers)
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
            .contentType(
                MediaTypeFactory.getMediaType(video).orElse(MediaType.APPLICATION_OCTET_STREAM)
            )
            .body(region)
    }

    private fun resourceRegion(video: Resource, headers: HttpHeaders): ResourceRegion {
        val contentLength = video.contentLength()
        val range = headers.range.firstOrNull()
        return if (range != null) {
            val start = range.getRangeStart(contentLength)
            val end = range.getRangeEnd(contentLength)
            val rangeLength = min(ChunkSize, end - start + 1)
            ResourceRegion(video, start, rangeLength)
        } else {
            val rangeLength = min(ChunkSize, contentLength)
            ResourceRegion(video, 0, rangeLength)
        }
    }

    companion object {
        const val ChunkSize = 1000000L
    }

}

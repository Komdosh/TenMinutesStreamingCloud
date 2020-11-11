package pro.komdosh.tmsc.auth.uaa.user.api

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import pro.komdosh.tmsc.auth.uaa.config.token.TokenRequest
import pro.komdosh.tmsc.auth.uaa.user.UserService
import pro.komdosh.tmsc.auth.uaa.user.model.CreateUserDto
import pro.komdosh.tmsc.auth.uaa.user.model.UserDto
import pro.komdosh.tmsc.auth.uaa.user.model.UserPrincipal
import reactor.core.publisher.Mono
import javax.validation.Valid


@RestController
@RequestMapping(value = ["/api/v1/uaa"], produces = [MediaType.APPLICATION_JSON_VALUE])
class UserController(
    private val userService: UserService
) {

    @PostMapping(value = ["/authenticate"])
    fun login(@RequestBody ar: AuthRequest): Mono<ResponseEntity<AuthResponse>> {
        return userService.authenticate(ar).map { ResponseEntity.ok(it) }
            .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build())
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    fun userInfo(@AuthenticationPrincipal userPrincipal: Mono<UserPrincipal>): Mono<ResponseEntity<UserPrincipal>> {
        return userPrincipal.map { ResponseEntity.ok(it) }
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody @Valid createUserDto: CreateUserDto): Mono<UserDto> =
        userService.create(createUserDto)

    @PostMapping("/token/refresh")
    fun refreshToken(@RequestBody request: TokenRequest): Mono<String> {
        return userService.refreshAccessToken(request)
    }

    @PostMapping("/token/invalidate")
    fun invalidateToken(@RequestBody request: TokenRequest): Mono<Boolean> {
        return userService.invalidateRefreshToken(request)
    }
}

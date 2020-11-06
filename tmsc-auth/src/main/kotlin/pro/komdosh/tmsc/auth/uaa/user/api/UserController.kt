package pro.komdosh.tmsc.auth.uaa.user.api

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import pro.komdosh.tmsc.auth.uaa.config.JwtTokenService
import pro.komdosh.tmsc.auth.uaa.user.*
import pro.komdosh.tmsc.auth.uaa.user.model.CreateUserDto
import pro.komdosh.tmsc.auth.uaa.user.model.User
import pro.komdosh.tmsc.auth.uaa.user.model.UserDto
import pro.komdosh.tmsc.auth.uaa.user.model.UserPrincipal
import reactor.core.publisher.Mono
import javax.validation.Valid

@RestController
@RequestMapping(value = ["/api/v1/uaa"], produces = [MediaType.APPLICATION_JSON_VALUE])
class UserController(
    private val jwtTokenService: JwtTokenService,
    private val passwordEncoder: PasswordEncoder,
    private val userService: UserService
) {

    @PostMapping(value = ["/login"])
    fun login(@RequestBody ar: AuthRequest): Mono<ResponseEntity<*>> {
        return userService.getUserByEmail(ar.email).map { userDetails: User ->
            if (passwordEncoder.matches(ar.password, userDetails.password)) {
                return@map ResponseEntity.ok(AuthResponse(jwtTokenService.generateToken(userDetails)))
            } else {
                return@map ResponseEntity.status(HttpStatus.UNAUTHORIZED).build<Any>()
            }
        }.defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build())
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    fun userInfo(@AuthenticationPrincipal userPrincipal: Mono<UserPrincipal>): Mono<ResponseEntity<*>> {
        return userPrincipal.map { ResponseEntity.ok(it) }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody @Valid createUserDto: CreateUserDto): Mono<UserDto> =
        userService.create(createUserDto)

}

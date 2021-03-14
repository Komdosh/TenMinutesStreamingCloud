# TenMinutesStreamingCloud

Spring Microservices with Webflux Example

### Configuration:

tmsc-config-server - manage spring cloud config

tmsc-discovery - discovery server to register and manage microservices

tmsc-gateway - url gateway, it knows services location and how handle url

### Microservices:

tmsc-health - microservice with health echo

tmsc-auth - simple authorization server

tmsc-stream-video - stream one video bytes by request

tmsc-compress - microservice that speak with tmsc-health through webclient

### Frontend:

tmsc-player - react app with video player

### Additional:

requests - Intellij IDEA requests example to microservices

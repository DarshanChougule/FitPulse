package com.fitness.gateway;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.fitness.gateway.service.RegesterRequest;
import com.fitness.gateway.service.UserService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class keycloakUserSyncFilter implements WebFilter {
    private final UserService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,WebFilterChain chain){
        String userId= exchange.getRequest().getHeaders().getFirst("X-User-ID");
        String token= exchange.getRequest().getHeaders().getFirst("Authorization");
        RegesterRequest regesterRequest= getuserDetails(token);
        if (userId==null) {
            userId=regesterRequest.getKeykloakid();
        }
        if(userId!=null && token!=null){
            return userService.validateUser(userId)
                    .flatMap(exist -> {
                        if(!exist){
                            // Regester User
                            
                            if(regesterRequest!=null){
                                return userService.regesterUser(regesterRequest)
                                        .then(Mono.empty());
                            }
                            else{
                                return Mono.empty();
                            }
                        }
                        else{
                            log.info("User Already Exist, Skipping sync.");
                            return Mono.empty();
                        }
                    })
                    .then(Mono.defer(()->{
                        ServerHttpRequest mutedRequest = exchange.getRequest().mutate()
                            .header("X-User-ID", regesterRequest.getKeykloakid())
                            .build();
                        return chain.filter(exchange.mutate().request(mutedRequest).build());  
                    }));
        }
        return chain.filter(exchange);
    }

    private RegesterRequest getuserDetails(String token) {
            try {
                String tokenWithoutBearer=token.replace("Bearer", "").trim();
                SignedJWT signedJWT=SignedJWT.parse(tokenWithoutBearer);
                JWTClaimsSet claims= signedJWT.getJWTClaimsSet();

                RegesterRequest regesterRequest=new RegesterRequest();
                regesterRequest.setEmail(claims.getStringClaim("email"));
                regesterRequest.setKeykloakid(claims.getStringClaim("sub"));
                regesterRequest.setPassword("dummy@123123");
                regesterRequest.setFirstName(claims.getStringClaim("given_name"));
                regesterRequest.setLastName(claims.getStringClaim("family_name"));

                return regesterRequest;
                
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
    }
}

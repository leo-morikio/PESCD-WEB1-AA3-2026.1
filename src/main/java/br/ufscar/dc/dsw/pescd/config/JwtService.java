package br.ufscar.dc.dsw.pescd.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    // Chave secreta usada para assinar os tokens - lida de application.properties (jwt.secret),
    // nunca deve ser hardcoded no código-fonte versionado.
    private final String secret;
    private final long expiracaoMs;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiracao-ms}") long expiracaoMs) {
        this.secret = secret;
        this.expiracaoMs = expiracaoMs;
    }

    private SecretKey getChave() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Gera um token a partir do nome de usuario
    public String gerarToken(String nomeUsuario) {
        return Jwts.builder()
                .subject(nomeUsuario)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiracaoMs))
                .signWith(getChave())
                .compact();
    }

    // Extrai o nome de usuário de dentro do token.
    // Retorna null se o token estiver malformado ou com assinatura inválida.
    public String extrairNomeUsuario(String token) {
        Claims claims = extrairClaims(token);
        if (claims == null) {
            return null;
        }
        return claims.getSubject();
    }

    // Verifica se o token é válido para o usuário informado
    public boolean tokenValido(String token, String nomeUsuario) {
        String usuarioDoToken = extrairNomeUsuario(token);
        if (usuarioDoToken == null) {
            return false;
        }
        return usuarioDoToken.equals(nomeUsuario) && !tokenExpirado(token);
    }

    private boolean tokenExpirado(String token) {
        Claims claims = extrairClaims(token);
        if (claims == null) {
            return true;
        }
        Date expiracao = claims.getExpiration();
        return expiracao.before(new Date());
    }

    // Faz o parsing/validacao de assinatura do token.
    // Retorna null (em vez de lançar exceção) se o token for invalido/malformado/expirado com formato incorreto,
    // para que o JwtFilter trate isso como "requisicao nao autenticada", nao como erro interno (500).
    private Claims extrairClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getChave())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException ex) {
            return null;
        }
    }
}

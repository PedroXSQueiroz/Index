package br.com.pedroxsqueiroz.Index.repo;

import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuração JPA auto-contida do módulo index.repo.
 *
 * @AutoConfigurationPackage registra o pacote deste módulo
 * (br.com.pedroxsqueiroz.Index.repo) na lista de pacotes monitorados
 * pelo auto-configure do Spring Boot. O JpaBaseConfiguration usa essa
 * lista como fallback para o escaneamento de @Entity quando @EntityScan
 * não está declarado explicitamente — o que evita o uso dessa anotação
 * problemática no Spring Boot 4.x.
 *
 * @EnableJpaRepositories registra as interfaces JpaRepository do módulo.
 *
 * Quando index.server faz o component scan de "br.com.pedroxsqueiroz",
 * esta classe é detectada e toda a infraestrutura JPA é configurada
 * sem que o servidor precise conhecer os pacotes internos do adapter.
 */
@Configuration
@AutoConfigurationPackage
@EnableJpaRepositories(basePackages = "br.com.pedroxsqueiroz.Index.repo")
public class IndexRepoConfiguration {
}

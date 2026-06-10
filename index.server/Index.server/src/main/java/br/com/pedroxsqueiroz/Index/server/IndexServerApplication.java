package br.com.pedroxsqueiroz.Index.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada da aplicação.
 *
 * O scanBasePackages cobre todo o namespace br.com.pedroxsqueiroz, portanto:
 *  - Os beans de index.embedding (EmbeddingAdapter, ChunckingAdapter, ContentStorageAdapter)
 *    são detectados pelo @Service/@Component scan.
 *  - A classe IndexRepoConfiguration (index.repo) é detectada pelo @Configuration scan
 *    e registra automaticamente as @Entity e JpaRepository daquele módulo.
 */
@SpringBootApplication(scanBasePackages = "br.com.pedroxsqueiroz")
public class IndexServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(IndexServerApplication.class, args);
    }
}

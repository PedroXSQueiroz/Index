package br.com.pedroxsqueiroz.Index.repo.content.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "contents")
public class ContentEntity {

    @Id
    @Column(nullable = false, updatable = false)
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    /** ID externo no sistema de storage (filesystem, S3, etc.) */
    @Column(name = "storage_id", nullable = false)
    private String storageId;

    private String name;

    private String description;

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ContentChunckEntity> chunks;
}

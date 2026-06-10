package br.com.pedroxsqueiroz.Index.repo.concept.entity;

import br.com.pedroxsqueiroz.Index.concept.constants.ConceptRelationTypesEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "concept_relations")
public class ConceptRelationEntity {

    @Id
    @Column(nullable = false, updatable = false)
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_id", nullable = false)
    private ConceptEntity origin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConceptRelationTypesEnum type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", nullable = false)
    private ConceptEntity target;
}

/*
 * AICare DI - Artificial Intelligence Care (Dynamic Inventory)
 * Todos os direitos reservados.
 */
package com.dminer.entities.autenticacao;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;

import com.dminer.entities.Profile;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "api_key")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@ToString
public class ApiKey implements Serializable {

    @Id
    @SequenceGenerator(name = "api_key_seq", sequenceName = "api_key_seq", allocationSize = 1)
    @GeneratedValue(generator = "api_key_seq", strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "data_criacao")
    private Timestamp dataCriacao;

    @Column(name = "data_vencimento")
    private Timestamp dataVencimento;

    @Column(name = "key", columnDefinition = "text")
    private String key;

    @Column(name = "nome")
    private String nome;

    @Column
    private boolean habilitado;

    @OneToOne
    private Profile perfil;

}

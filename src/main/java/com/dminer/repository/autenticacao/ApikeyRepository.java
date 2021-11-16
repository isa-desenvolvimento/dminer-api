// /*
//  * AICare DI - Artificial Intelligence Care (Dynamic Inventory)
//  * Todos os direitos reservados.
//  */
// package com.dminer.repository.autenticacao;

// import com.dminer.entities.autenticacao.ApiKey;

// import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.PagingAndSortingRepository;
// import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;

// /**
//  *
//  * @author Paulo Collares
//  */
// @Repository
// public interface ApikeyRepository extends PagingAndSortingRepository<ApiKey, Integer>, JpaSpecificationExecutor<ApiKey> {

//     @Query("SELECT CASE WHEN t.habilitado=true THEN true ELSE false END FROM ApiKey t WHERE key = :key")
//     public boolean isHabilitado(@Param("key") String key);
    
//     public ApiKey findByKey(String key);
// }

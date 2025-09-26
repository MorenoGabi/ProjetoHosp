package com.meuprojeto.dbhandlers;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * Interface para tratar inserts/updates/deletes específicos por tabela.
 * keys do mapa são nomes de colunas (em lower-case), values são strings vindas dos campos do formulário.
 */
public interface TableHandler {
    // Inserir; retorna null ou id gerado (se houver)
    Integer insert(Connection conn, List<String> columnOrder, Map<String,String> values) throws Exception;

    // Atualizar registro existente
    void update(Connection conn, Object idValue, List<String> columnOrder, Map<String,String> values) throws Exception;

    // Deletar
    void delete(Connection conn, Object idValue) throws Exception;
}


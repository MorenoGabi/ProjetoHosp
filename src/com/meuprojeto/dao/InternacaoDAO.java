package  com.meuprojeto.dao;

import com.meuprojeto.model.Internacoes;

import java.time.LocalDateTime;
import java.util.List;

public interface InternacaoDAO {
    Internacoes findById (int id) throws Exception;
    List<Internacoes> findAll  () throws Exception;
    List <Internacoes> findByPaciente (int pacienteId) throws Exception;
    List <Internacoes> findByActiveInternacao () throws Exception;
    List <Internacoes> findByDateRange (LocalDateTime from, LocalDateTime to) throws Exception;
    void insert (Internacoes i) throws Exception;
    void update (Internacoes i) throws Exception;
    void delete (int id) throws Exception;
}

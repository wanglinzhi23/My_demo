package intellif.dao;

import intellif.database.entity.PoliceCase;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PoliceCaseDao extends JpaRepository<PoliceCase, Long> {


}

package intellif.dao;

import intellif.database.entity.OauthClientDetails;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by yangboz on 11/18/15.
 */
public interface OauthClientDetailsDao extends JpaRepository<OauthClientDetails, Long> {
}

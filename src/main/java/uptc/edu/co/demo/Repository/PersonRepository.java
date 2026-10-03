package uptc.edu.co.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uptc.edu.co.demo.Entities.PersonEntity;

import java.util.List;

public interface PersonRepository extends JpaRepository<PersonEntity, Integer> {
    List<PersonEntity> findByIdGreaterThanEqualAndIdLessThanOrderByIdAsc(int desde, int hasta);
}
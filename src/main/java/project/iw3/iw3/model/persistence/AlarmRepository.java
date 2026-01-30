package project.iw3.iw3.model.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import project.iw3.iw3.model.Alarm;


@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long>  {

}
